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
package org.eclipse.om2m.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.om2m.binding.service.RestClientService;
import org.eclipse.om2m.commons.constants.CSEType;
import org.eclipse.om2m.commons.constants.Constants;
import org.eclipse.om2m.core.comm.RestClient;
import org.eclipse.om2m.core.datamapper.DataMapperSelector;
import org.eclipse.om2m.core.interworking.IpeSelector;
import org.eclipse.om2m.core.persistence.PersistenceService;
import org.eclipse.om2m.core.router.Router;
import org.eclipse.om2m.core.service.CseService;
import org.eclipse.om2m.core.thread.CoreExecutor;
import org.eclipse.om2m.datamapping.service.DataMapperService;
import org.eclipse.om2m.interworking.service.InterworkingService;
import org.eclipse.om2m.persistence.service.DBService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Manages the starting and stopping of the bundle.
 */
public class Activator implements BundleActivator {
	private static BundleContext context;
	/** Logger */
	private static Log LOGGER = LogFactory.getLog(Activator.class);
	/** IPE service tracker */
	private ServiceTracker<Object, Object> ipeServiceTracker;
	/** Rest Client service tracker */
	private ServiceTracker<Object, Object> restClientServiceTracker;
	/** Data Mapper Service tracker */
	private ServiceTracker<Object, Object> dataMapperServiceTracker;
	/** Database Service tracker */
	private ServiceTracker<Object, Object> databaseServiceTracker;

	public void start(BundleContext bundleContext) throws Exception {
		LOGGER.info("Starting CSE...");
		Activator.context = bundleContext;

		// Track the Data Mapper Service 
		dataMapperServiceTracker = new ServiceTracker<Object,Object>(bundleContext, DataMapperService.class.getName(), null){

			@Override
			public Object addingService(ServiceReference<Object> reference) {
				DataMapperService dataMapper = (DataMapperService) this.context.getService(reference);
				LOGGER.info("Added Data Mapper Service: " + dataMapper.getServiceDataType());
				DataMapperSelector.getDataMapperList().put(dataMapper.getServiceDataType(), dataMapper);
				return dataMapper;
			}

			@Override
			public void removedService(ServiceReference<Object> reference,
					Object service) {
				DataMapperService dataMapper = (DataMapperService) service;
				LOGGER.info("Removed Data Mapper Service: " + dataMapper.getServiceDataType());
				DataMapperSelector.getDataMapperList().remove(dataMapper.getServiceDataType());
			}

		} ;
		dataMapperServiceTracker.open();

		// track the persistence database service
		databaseServiceTracker = new ServiceTracker<Object, Object>(bundleContext, DBService.class.getName(), null){

			@Override
			public Object addingService(ServiceReference<Object> reference) {
				LOGGER.info("DataBase persistence service discovered");
				DBService dbService = (DBService) this.context.getService(reference);
				PersistenceService.getInstance().setDbService(dbService);

				// Post the start routine in the CoreExecutor
				CoreExecutor.postThread(new Runnable() {
					@Override
					public void run() {
						try {
							CSEInitializer.init();
						} catch (InterruptedException e) {
							LOGGER.error("Error in CSEInitializer", e);
						}
						LOGGER.info("Registering CseService...");
						context.registerService(CseService.class.getName(), new Router(), null);		
						LOGGER.info("CSE Started");		
					}
				});
				
				return dbService;
			}

			@Override
			public void removedService(ServiceReference<Object> reference,
					Object service) {
				LOGGER.info("Database persistence service removed.");
				PersistenceService.getInstance().setDbService(null);
			}

		};
		databaseServiceTracker.open();

		// track the rest client service
		restClientServiceTracker = new ServiceTracker<Object,Object>(bundleContext, RestClientService.class.getName(), null){

			public Object addingService(org.osgi.framework.ServiceReference<Object> reference) {
				RestClientService service = (RestClientService) this.context.getService(reference);
				RestClient.getRestClients().put(service.getProtocol(), service);
				LOGGER.info("Rest client service discovered. Protocol: " + service.getProtocol());
				return service;
			};

			public void removedService(org.osgi.framework.ServiceReference<Object> reference, Object service) {
				LOGGER.info("Rest client service removed");
				RestClientService restClientService = (RestClientService) service;
				RestClient.getRestClients().remove(restClientService.getProtocol());
			};

		} ;
		restClientServiceTracker.open();
		
		// track the interworking serivce
		ipeServiceTracker = new ServiceTracker<Object, Object>(bundleContext, InterworkingService.class.getName(), null){
			
			public Object addingService(org.osgi.framework.ServiceReference<Object> reference) {
				InterworkingService service = (InterworkingService) this.context.getService(reference);
				IpeSelector.getInterworkingList().put(service.getAPOCPath(), service);
				LOGGER.info("IPE service discovered: " + service.getAPOCPath());
				return service;
			}
			
			@Override
			public void removedService(ServiceReference<Object> reference,
					Object service) {
				InterworkingService ipeService = (InterworkingService) service;
				LOGGER.info("IPE service removed: " + ipeService.getAPOCPath());
				IpeSelector.getInterworkingList().remove(ipeService.getAPOCPath());
			}
			
		};
		ipeServiceTracker.open();

	}

	public void stop(BundleContext bundleContext) throws Exception {
		LOGGER.info("Stopping CSE");
		if(!Constants.CSE_TYPE.equals(CSEType.IN_CSE)){
			CSEInitializer.unregisterCse();			
		}
	}

	protected static BundleContext getContext(){
		return context;
	}

}
