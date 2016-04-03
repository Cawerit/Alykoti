package com.example.alykoti.models;

import java.util.Date;

/**
 * IResource johon tulleita muutoksia voi seurata
 */
public interface IUpdatable {
	String getUniqueKey();
	long getUpdated();
}
