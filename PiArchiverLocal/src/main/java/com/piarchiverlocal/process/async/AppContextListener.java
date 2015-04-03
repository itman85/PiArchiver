package com.piarchiverlocal.process.async;

import com.piarchiverlocal.elasticsearch.ClientProvider;
import com.piarchiverlocal.util.PiConfig;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@WebListener
public class AppContextListener implements ServletContextListener {

	public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("AppContextListener contextInitialized");
		// create the thread pool
		ThreadPoolExecutor executor = new ThreadPoolExecutor(100, 200, 50000L,
				TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(100));
		servletContextEvent.getServletContext().setAttribute("executor",
				executor);

       /* servletContextEvent.getServletContext().setAttribute("pathapp",
                servletContextEvent.getServletContext().getRealPath("/"));*/
        //set root path for log
        System.setProperty("rootPath", servletContextEvent.getServletContext().getRealPath("/"));
        //init config file at very first moment
        PiConfig.getInstance();
        //init node client for elasticsearch
        ClientProvider.instance().prepareClient();
	}

	public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("AppContextListener contextDestroyed");
		ThreadPoolExecutor executor = (ThreadPoolExecutor) servletContextEvent
				.getServletContext().getAttribute("executor");
		executor.shutdown();
        //close node for elasticsearch
        ClientProvider.instance().closeNode();
	}

}
