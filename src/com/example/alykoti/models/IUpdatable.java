package com.example.alykoti.models;

import java.util.Date;

/**
 * IResource johon tulleita muutoksia voi seurata
 */
public interface IUpdatable {
	boolean updatedAfter(IUpdatable oldVersion);
	long getUpdated();
}
