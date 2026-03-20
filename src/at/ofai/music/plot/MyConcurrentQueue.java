package at.ofai.music.plot;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Simply extends a ConcurrentLinkedQueue only with a getLast() method 
 * returning the last added object.
 * 
 * @author Werner Goebl, Nov 2005
 *
 * @param <E>
 */
public class MyConcurrentQueue<E> extends ConcurrentLinkedQueue<E> {

	private static final long serialVersionUID = 1L;
	private E gco = null;
	
	public boolean add(E e) {
		gco = e;
		return super.add(e);
	} // add
	
	public E getLast() {
		return gco;
	} // getLast
	
} // MyConcurrentQueue