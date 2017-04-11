/*******************************************************************************
 * Copyright (c) 2013-2016 LAAS-CNRS (www.laas.fr)
 * 7 Colonel Roche 31077 Toulouse - France
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Initial Contributors:
 *     Thierry Monteil : Project manager, technical co-manager
 *     Mahdi Ben Alaya : Technical co-manager
 *     Samir Medjiah : Technical co-manager
 *     Khalil Drira : Strategy expert
 *     Guillaume Garzone : Developer
 *     François Aïssaoui : Developer
 *
 * New contributors :
 *******************************************************************************/
package org.eclipse.om2m.core.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.commons.constants.Constants;

/**
 * This class provides a management of thread policies. It is initialized using 
 * the Constants.MAX_THREAD_POOL_SIZE and avoid creating too much thread and overload 
 * an instance. 
 * 
 */
public class CoreExecutor {
	
	/** Default logger */
	private static final Log LOGGER = LogFactory.getLog(CoreExecutor.class.getName());
	/** The ExecutorService with the applied thread policy */
	private static final ExecutorService threadPool;

	/**
	 * Initialize the threadPool with the specific policy
	 */
	static {
		// Use minimum 2 threads
		int maximumPoolSize = Constants.MAX_THREAD_POOL_SIZE > 2 ? Constants.MAX_THREAD_POOL_SIZE: 2; 
		// Provide 10% of thread kept alive for better performance
		int corePoolSize = maximumPoolSize / 10;
		LOGGER.info("Creating thread pool with corePoolSize=" + corePoolSize + 
				" & maximumSize=" + maximumPoolSize);
		// Keep alive the threads for a minute in the pool
		threadPool = new ThreadPoolExecutor(
				corePoolSize, maximumPoolSize, 
				60L, TimeUnit.SECONDS, 
				new SynchronousQueue<Runnable>()
		);
	}
	
	/**
	 * Submit the operation to the executor service. 
	 * It will return a Future<T> object. To access the result of the
	 * operation, use the get() method of the Future<T> object that will
	 * block until the result is not available. Then, it will provide the 
	 * result of the operation of type <T>.
	 * @param callable the operation to perform with the specific type
	 * @return the Future<T> object, use the get() method to retrieve the result
	 */
	public static <T> Future<T> submit(Callable<T> callable){
		return threadPool.submit(callable);
	}
	
	/** 
	 * Post the runnable into the executor service.
	 * @param runnable the operation to be performed
	 */
	public static void postThread(Runnable runnable){
		threadPool.execute(runnable);
	}
	
}
