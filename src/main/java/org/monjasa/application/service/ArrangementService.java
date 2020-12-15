package org.monjasa.application.service;

import org.monjasa.application.model.Arrangement;

import java.util.List;

public interface ArrangementService {

    List<Arrangement> findAll();

    Arrangement findByName(String name);
}