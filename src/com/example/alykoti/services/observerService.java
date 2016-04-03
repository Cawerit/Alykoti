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

	private final List<IUpdatable> changedData = new ArrayList<>();

	/**
	 * Tarkistaa onko resurssiin tullut muutoksia annetun ajan jälkeen
	 */
	private boolean hasChanges(IUpdatable o){
		synchronized (changedData) {
			//Käydään lista läpi lopusta alkuun, koska muutokset laitetaan aina listan loppuun
			//(sieltä ne löytyvät luultavasti nopeammin)
			int i = changedData.size();
			while(--i >= 0) {
				IUpdatable change = changedData.get(i);
				if (change.equals(o)) {
					//System.out.println("Tutkitaan objektia " + o + " " + change.updatedAfter(after));
					boolean result = change.updatedAfter(o);
					if(result){
						System.out.println("Has changes! " + change + " " + o);
					}
					return result;
				}
			}
			return false;
		}
	}

	/**
	 * Lisää päivitetyn resurssin listaan
	 * @param o Resurssi jonka muutoksia halutaan tarkastella
	 */
	public void update(IUpdatable o){
		synchronized (changedData) {
			int index = changedData.indexOf(o);
			if(index != -1){
				changedData.set(index, o);
			} else {
				changedData.add(o);
				if (changedData.size() > MAX_QUEUE_LENGTH) {
					changedData.remove(0);
				}
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
		void onNext(IUpdatable oldData);
	}

	/**
	 * Wrapper luokka IObserverille, joka sisältää hieman lisää dataa jolla voidaan välillä
	 * tyhjentää observereita (vältetään memory leakit).
	 */
	public class ObserverCollection {

		private final List<WrappedObserver> observers = new ArrayList<>();

		public void update(){
			for(WrappedObserver o : observers){
				if(hasChanges(o.listens)){
					o.onNext();
				}
			}
		}

		public void subscribe(IUpdatable listen, IObserver observer){
			this.observers.add(new WrappedObserver(listen, observer));
		}
	}

	private static class WrappedObserver {

		private final IObserver wrapped;
		private final IUpdatable listens;

		public WrappedObserver(IUpdatable listen, IObserver observer){
			listens = listen;
			wrapped = observer;
		}

		private void onNext(){
			wrapped.onNext(listens);
		}
	}
}
