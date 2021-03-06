package com.jiema0217.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jiema0217.gulimall.product.service.CategoryBrandRelationService;
import com.jiema0217.gulimall.product.vo.Catelog2Vo;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiema0217.common.utils.PageUtils;
import com.jiema0217.common.utils.Query;

import com.jiema0217.gulimall.product.dao.CategoryDao;
import com.jiema0217.gulimall.product.entity.CategoryEntity;
import com.jiema0217.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    //private Map<String, Object> cache = new HashMap<>();

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    RedissonClient redisson;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1、查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);

        //2、组装成父子的树形结构
        //2.1、找到所有的一级分类
        List<CategoryEntity> level1Menus = entities.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == 0
        ).map((menu -> {
            menu.setChildren(getChildrens(menu, entities));
            return menu;
        })).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());
        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 1、检查当前删除的菜单，是否被别的地方引用

        //逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);
        return paths.toArray(new Long[parentPath.size()]);
    }

    /**
     * 级联更新所有关联的数据
     * @CacheEvict:缓存失效模式
     * 1、同时进行多种缓存操作 @Caching
     * 2、指定删除某个分区下的所有数据 @CacheEvict(value = "category", allEntries = true)
     * 3、存储同一类型的数据，都可以指定成同一个分区。分区名默认就是缓存的前缀
     *
     * @param category
     */
    //@Caching(evict = {
    //        @CacheEvict(value = {"category"}, key = "'getLevel1Categorys'"),
    //        @CacheEvict(value = {"category"}, key = "'getCatalogJson'")
    //})
    @CacheEvict(value = "category", allEntries = true)  //失效模式
    //@CachePut   //双写模式
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
        //同时修改缓存中的数据
        //redis.del("catalogJson");等待下次主动查询，进行更新
    }


    /**
     * 1、每一个需要缓存的数据都来指定要放到哪个名字的缓存。【缓存分区】，按照业务类型分
     * 2、代表当前方法结果需要缓存，如果缓存中有，方法不用调用；如果缓存中没有，会调用方法，最后将方法的结果放入缓存
     * 3、默认行为
     *      1）、如果缓存中有，方法不用调用
     *      2）、key是自动默认生成的。缓存的名字::SimpleKey [](自主生成的key值)
     *      3）、value的值。默认使用jdk序列化机制，将序列化后的数据存到redis
     *      4）、ttl：-1。永不过期
     *   自定义：
     *      1）、指定生成的缓存使用的key: key属性指定，接受一个SpEL
     *      2）、指定缓存的数据存活时间: 配置文件中修改
     *      3）、将数据保存为json格式
     * 4、Spring-Cache的不足：
     *      1）、读模式：
     *          缓存穿透：查询一个null的数据。解决方案：缓存空数据 cache-null-values=true
     *          缓存击穿：大量并发同时查询一个正好过期的数据。解决方案：加锁。默认是无加锁的 sync = true（加锁，解决击穿）
     *          缓存雪崩；大量的key同时过期。解决方案：加随机时间。加上过期时间。 redis.time-to-live=3600000
     *      2）、写模式：（缓存与数据库一致）
     *          1、读写加锁。
     *          2、引入Canal，感知到MySQL的更新去更新数据库
     *          3、读多写多，直接去数据库查询
     *
     *     原理：CacheManager(RedisCacheManager)->Cache(RedisCache)->Cache负责缓存的读写
     *  总结：
     *      常规数据（读多写少，一致性，即时性要求不高的数据），完全可以使用Spring-Cache。写模式（只要缓存的数据有过期时间就足够了）
     *      特殊数据：特殊设计
     *
     */
    @Cacheable(value = {"category"}, key = "#root.method.name",sync = true)
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        System.out.println("getLevel1Categorys");
        List<CategoryEntity> categoryEntities = this.baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntities;
    }

    //TODO 产生堆外内存溢出，OutOfDirectMemoryError
    //1）、Spring Boot2.0以后默认使用lettuce作为操作redis的客户端。它使用netty进行网络通信
    //2）、lettuce的bug导致堆外内存溢出，neetty如果没有指定堆外内存，默认使用-Xmx
    // 可以通过 -Dio.netty.maxDirectMemory 进行设置
    //解决方案：不能使用-Dio.netty.maxDirectMemory 只去调大堆外内存
    //1）、升级lettuce客户端；2）、切换使用jedis

    @Cacheable(value = "category", key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        System.out.println("查询了数据库。。。。");
        List<CategoryEntity> selectList = this.baseMapper.selectList(null);

        //1、查出所有一级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);

        //2、封装数据
        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1、每一个一级分类，查到这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            //2、封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    //1、找当前二级分类的三级分类，封装成vo
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());
                    if (level3Catelog != null) {
                        List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
                            //2、封装成指定格式
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(collect);
                    }
                    level3Catelog.stream();
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));

        return parent_cid;
    }

    public Map<String, List<Catelog2Vo>> getCatalogJson2() {
        /**
         * 1、空结果缓存：解决缓存穿透
         * 2、设置过期时间（加随机值），解决缓存雪崩
         * 3、加锁：解决缓存击穿
         */
        //1、加入缓存逻辑，缓存中存的数据是json
        String catalogJson = redisTemplate.opsForValue().get("catalogJson");
        if (StringUtils.isEmpty(catalogJson)) {
            //2、缓存中没有，数据库查询
            Map<String, List<Catelog2Vo>> catalogJsonFromDb = getCatalogJsonFromDbWithRedissonLock();
            //3、查到的数据放入缓存，将对象转为json放在缓存
            System.out.println("缓存不命中，查询数据库...");
            return catalogJsonFromDb;
        } else {
            System.out.println("缓存命中...");

            Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
            return result;
        }
    }


    private Map<String, List<Catelog2Vo>> getDataFromDb() {
        String catalogJson = redisTemplate.opsForValue().get("catalogJson");
        if (!StringUtils.isEmpty(catalogJson)) {
            Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
            return result;
        }
        System.out.println("查询了数据库。。。。");
        /**
         * 1、将数据库的多次查询变为一次
         */
        List<CategoryEntity> selectList = this.baseMapper.selectList(null);

        //1、查出所有一级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);

        //2、封装数据
        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1、每一个一级分类，查到这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
            //2、封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    //1、找当前二级分类的三级分类，封装成vo
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());
                    if (level3Catelog != null) {
                        List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
                            //2、封装成指定格式
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(collect);
                    }
                    level3Catelog.stream();
                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));

        //cache.put("getCatalogJson", parent_cid);
        redisTemplate.opsForValue().set("catalogJson", JSON.toJSONString(parent_cid), 1, TimeUnit.DAYS);
        return parent_cid;
    }

    /**
     * 缓存里边的数据如何与数据库保持一致
     * 缓存数据一致性问题
     * 1）、双写模式
     * 2）、失效模式
     *
     * @return
     */
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedissonLock() {

        //1、占分布式锁，去redis占坑；设置过期时间
        //2、锁的名字，锁的粒度，越细越快。
        //锁的粒度，具体缓存的是某个数据
        RLock lock = redisson.getLock("CatalogJson-lock");
        lock.lock();
        Map<String, List<Catelog2Vo>> dataFromDb;
        try {
            dataFromDb = getDataFromDb();
        } finally {
            lock.unlock();
        }

        return dataFromDb;
    }

    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithRedisLock() {

        String uuid = UUID.randomUUID().toString();
        //1、占分布式锁，去redis占坑；设置过期时间
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            System.out.println("获取分布式锁成功。。。");
            Map<String, List<Catelog2Vo>> dataFromDb;
            //加锁成功,执行业务
            try {
                dataFromDb = getDataFromDb();
            } finally {
                //lua脚本解锁
                String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                //原子删除锁
                Long lock1 = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), uuid);
            }
            ////删除锁
            //String lockValue = redisTemplate.opsForValue().get("lock");
            //if (lockValue.equals(uuid)) {
            //    //删除自己的锁
            //    redisTemplate.delete("lock");
            //}
            return dataFromDb;
        }
        System.out.println("获取分布式锁失败，等待重试");
        //加锁失败，重试机制
        //休眠100ms
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return getCatalogJsonFromDbWithLocalLock();//自旋
    }

    //从数据库查询并封装分类数据
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDbWithLocalLock() {
        //1、如果缓存中有，就用缓存的(本地缓存)
        //Map<String, List<Catelog2Vo>> getCatalogJson = (Map<String, List<Catelog2Vo>>) cache.get("getCatalogJson");
        //if (cache.get("getCatalogJson") != null) {
        //    return (Map<String, List<Catelog2Vo>>) cache.get("getCatalogJson");
        //}

        //只要是同一把锁，就能锁住这个锁的所有线程
        //1、synchronized (this)：Spring Boot所有的组件在容器中都是单例的
        //TODO 本地锁：synchronized，JUC（Lock）,在分布式服务，想要锁住所有，必须使用分布式锁
        synchronized (this) {
            //得到锁以后，我们应该再去缓存中确定一次，如果没有才需要继续查询
            return getDataFromDb();
        }
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid().equals(parent_cid)).collect(Collectors.toList());
        //return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
        return collect;
    }

    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        //1、手机当前节点id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }

    //递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(CategoryEntity -> {
            return CategoryEntity.getParentCid().equals(root.getCatId());
        }).map(categoryEntity -> {
            //1、找到子菜单
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            //2、菜单的排序
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());
        return children;
    }

}