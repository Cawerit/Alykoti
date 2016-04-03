package com.example.alykoti.services;

import com.example.alykoti.models.IUpdatable;

import java.util.*;

/**
 * Service joka vastaa erilaisten muutosten lähettämisestä ja seuraamisesta
 * esimerkiksi laitteiden tilaan.
 */
public class ObserverService {
	/**
	 * Maksimimäärä muistissa pidettävistä muutoksista, jolla varmistetaan
	 * ettei ajan mittaan synny liikaa päivittynyttä dataa ja sen myötä
	 * ylimääräistä roskaa.
	 */
	private static final int MAX_QUEUE_LENGTH = 50;

	private final List<WrappedObservable> changedData = new ArrayList<>();
	private int findByKey(String key){
		int i = changedData.size();
		while(--i >= 0) {
			WrappedObservable change = changedData.get(i);
			if(change.key.equals(key)) return i;
		}
		return -1;
	}

	/**
	 * Tarkistaa onko resurssiin tullut muutoksia annetun ajan jälkeen
	 */
	private boolean hasChanges(WrappedObserver observer){
		synchronized (changedData) {
			//Käydään lista läpi lopusta alkuun, koska muutokset laitetaan aina listan loppuun
			//(sieltä ne löytyvät luultavasti nopeammin)
			int i = findByKey(observer.key);
			if(i == -1) {
				return false;
			}
			WrappedObservable change = changedData.get(i);
				//System.out.println("Tutkitaan objektia " + o + " " + change.updatedAfter(after));
			boolean result = change.updated > observer.updated;
			if(result){
				System.out.println("Found changes in " + change.key + "! Informing the observer.");
			}
			return result;
		}
	}

	/**
	 * Lisää päivitetyn resurssin listaan
	 * @param o Resurssi jonka muutoksia halutaan tarkastella
	 */
	public void update(IUpdatable o){
		synchronized (changedData) {
			changedData.add(new WrappedObservable(o.getUniqueKey(), o.getUpdated()));
			if (changedData.size() > MAX_QUEUE_LENGTH) {
				changedData.remove(0);
			}
		}
	}

	public ObserverCollection createObserverCollection(){
		return new ObserverCollection();
	}

	//Tämä, kuten muutkin servicet on tärkeä jakaa koko ohjelman kesken
	//eikä luoda uutta objektia jokaiselle käyttäjälle
	private static ObserverService instance;
	public static ObserverService getInstance(){
		if(instance == null) {
			instance = new ObserverService();
		}
		return instance;
	}

	@FunctionalInterface
	public interface IObserver {
		void onNext();
	}

	/**
	 * Wrapper luokka IObserverille, joka sisältää hieman lisää dataa jolla voidaan välillä
	 * tyhjentää observereita (vältetään memory leakit).
	 */
	public class ObserverCollection {

		private final List<WrappedObserver> observers = new ArrayList<>();

		public void update(){
			for(WrappedObserver o : observers){
				if(hasChanges(o)){
					o.onNext();
				}
			}
		}

		public void subscribe(IUpdatable listen, IObserver observer){
			this.observers.add(new WrappedObserver(listen, observer));
		}
	}

	private static class WrappedObserver {

		public final IObserver wrapped;
		public final String key;
		protected long updated;

		public WrappedObserver(IUpdatable listen, IObserver observer){
			key = listen.getUniqueKey();
			wrapped = observer;
		}

		private void onNext(){
			updated = new Date().getTime();
			wrapped.onNext();
		}
	}

	private class WrappedObservable {
		public final String key;
		protected long updated;

		public WrappedObservable(String key, long updated) {
			this.key = key;
			this.updated = updated;
		}
	}

}
