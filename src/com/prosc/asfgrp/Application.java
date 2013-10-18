package com.prosc.asfgrp;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

/**
 * Created by IntelliJ IDEA. User: val Date: 7/2/12 Time: 1:21 PM
 */
public class Application extends WebApplication {
	@Override
	public Class<? extends Page> getHomePage() {
		return Submit.class;
	}

	@Override
	protected void init() {
		super.init();

		getMarkupSettings().setDefaultMarkupEncoding("utf-8");

		mountBookmarkablePage("/apply", Submit.class);
		mountBookmarkablePage("/thankyou", ThankYou.class);
	}
}
