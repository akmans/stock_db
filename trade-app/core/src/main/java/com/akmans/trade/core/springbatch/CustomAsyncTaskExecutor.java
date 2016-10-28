package com.akmans.trade.core.springbatch;

import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@SuppressWarnings("serial")
public class CustomAsyncTaskExecutor extends SimpleAsyncTaskExecutor {
	@Override
	public void execute(final Runnable r) {
		final Authentication a = SecurityContextHolder.getContext().getAuthentication();

		super.execute(new Runnable() {
			public void run() {
				try {
					SecurityContext ctx = SecurityContextHolder.createEmptyContext();
					ctx.setAuthentication(a);
					SecurityContextHolder.setContext(ctx);
					r.run();
				} finally {
					SecurityContextHolder.clearContext();
				}
			}
		});
	}
}
