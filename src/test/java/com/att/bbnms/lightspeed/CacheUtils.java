package com.att.bbnms.lightspeed;
import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.openjpa.conf.OpenJPAConfiguration;
import org.apache.openjpa.datacache.DataCache;
import org.apache.openjpa.datacache.DataCachePCData;
import org.apache.openjpa.meta.ClassMetaData;
import org.apache.openjpa.meta.MetaDataRepository;
import org.apache.openjpa.persistence.JPAFacadeHelper;
import org.apache.openjpa.persistence.OpenJPAEntityManagerFactorySPI;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.openjpa.persistence.StoreCacheImpl;


public class CacheUtils {

	static public DataCachePCData getObjectFromCache(EntityManager entityManager, Object obj, Object oid) {
		DataCache cache = getCache(entityManager, obj.getClass());
		
		// objects are stored by openJpaId
		Object openJpaObjectId = getOpenJPAId(entityManager, obj, oid);
		Object o = JPAFacadeHelper.fromOpenJPAObjectId(openJpaObjectId);
		
		DataCachePCData cc = cache.get(openJpaObjectId);
		
		return cache.get(openJpaObjectId);
		//return cache.get(openJpaObjectId).getImplData();
	}
	
	static public boolean objectExistsInL2Cache(EntityManager entityManager, Object obj, Object oid) {
		DataCache cache = getCache(entityManager, obj.getClass());
		
		// objects are stored by openJpaId
		Object openJpaObjectId = getOpenJPAId(entityManager, obj, oid);
		return cache.contains(openJpaObjectId);
	}
	
	/**
     * Gets the data cache for the given class.
     */
//	static public DataCache getCache(EntityManager entityManager, Class<?> cls) {
//    	EntityManagerFactory emf = entityManager.getEntityManagerFactory();
//    	OpenJPAConfiguration conf = ((OpenJPAEntityManagerFactorySPI) emf).getConfiguration();
//    	
//        String name = getConfiguredDataCacheName(entityManager, cls);
//        return conf.getDataCacheManagerInstance().getDataCache(name);
//    }

	static public DataCache getCache(EntityManager entityManager, Class<?> cls) {
		String name = getConfiguredDataCacheName(entityManager, cls);
        StoreCacheImpl storeCache =
        	(StoreCacheImpl)OpenJPAPersistence.cast(entityManager.getEntityManagerFactory()).getStoreCache(name);
        
        return storeCache.getDelegate();
    }
	
    /**
     * Gets the configured name of the cache for the given class.
     */
	static public String getConfiguredDataCacheName(EntityManager entityManager, Class<?> cls) {
    	EntityManagerFactory emf = entityManager.getEntityManagerFactory();
    	OpenJPAConfiguration conf = ((OpenJPAEntityManagerFactorySPI) emf).getConfiguration();
    	
        MetaDataRepository mdr = conf.getMetaDataRepositoryInstance();
        ClassMetaData meta = mdr.getMetaData(cls, null, true);
        return meta.getDataCacheName();
    }
	
//	static public Object getOpenJPAId(EntityManager entityManager, Object obj, Object oid) {
//    	EntityManagerFactory emf = entityManager.getEntityManagerFactory();
//    	OpenJPAConfiguration conf = ((OpenJPAEntityManagerFactorySPI) emf).getConfiguration();
//    	
//        ClassMetaData meta = conf.getMetaDataRepositoryInstance().getCachedMetaData(obj.getClass());
//        assertNotNull(meta);
//        Object openJpaOid = JPAFacadeHelper.toOpenJPAObjectId(meta, oid);
//        assertNotNull(openJpaOid);
//        return openJpaOid;
//    }
	
	static public Object getOpenJPAId(EntityManager entityManager, Object obj, Object oid) {
		ClassMetaData classMetaData = JPAFacadeHelper.getMetaData(entityManager, obj.getClass());
		Object openJpaOid = JPAFacadeHelper.toOpenJPAObjectId(classMetaData, oid);
		
        return openJpaOid;
    }
}
