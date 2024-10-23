package org.demo.useraccounts.repository;

import org.demo.useraccounts.model.BaseEntity;
import org.reactivestreams.Publisher;
import org.springframework.core.Ordered;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.data.r2dbc.mapping.event.BeforeSaveCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.time.LocalDateTime;

@Component
class BaseEntityCallback implements BeforeSaveCallback<BaseEntity<? extends Serializable>>, Ordered {

	@Override
	public Publisher<BaseEntity<? extends Serializable>> onBeforeSave(BaseEntity<? extends Serializable> entity, OutboundRow row, SqlIdentifier table) {

		if(entity.getCreatedOn()==null)
			entity.setCreatedOn(LocalDateTime.now());
		return  Mono.just(entity);
	}

	@Override
	public int getOrder() {
		return 100;                                                                  
	}
}