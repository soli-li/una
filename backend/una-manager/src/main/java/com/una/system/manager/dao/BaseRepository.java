package com.una.system.manager.dao;

import java.io.Serializable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T, IDTypeT extends Serializable>
    extends CrudRepository<T, IDTypeT> {
}
