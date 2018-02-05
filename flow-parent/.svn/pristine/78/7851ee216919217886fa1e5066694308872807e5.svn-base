package com.aspire.etl.flowdesinger;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;

public class Double extends Rectangle2D implements Serializable, Cloneable{
	/**
	 * The x coordinate of this <code>Rectangle2D</code>.
	 * @since 1.2
	 */
	public double x;

	/**
	 * The y coordinate of this <code>Rectangle2D</code>.
	 * @since 1.2
	 */
	public double y;

	/**
	 * The width of this <code>Rectangle2D</code>.
	 * @since 1.2
	 */
	public double width;

	/**
	 * The height of this <code>Rectangle2D</code>.
	 * @since 1.2
	 */
	public double height;

	/**
	 * Constructs a new <code>Rectangle2D</code>, initialized to
         * location (0,&nbsp;0) and size (0,&nbsp;0).
	 * @since 1.2
	 */
	public Double() {
	}

	/**
	 * Constructs and initializes a <code>Rectangle2D</code> 
         * from the specified double coordinates.
	 * @param x,&nbsp;y the coordinates of the upper left corner
         * of the newly constructed <code>Rectangle2D</code>
	 * @param w the width of the
         * newly constructed <code>Rectangle2D</code>
	 * @param h the height of the
         * newly constructed <code>Rectangle2D</code>
	 * @since 1.2
	 */
	public Double(double x, double y, double w, double h) {
	    setRect(x, y, w, h);
	}

	/**
	 * Returns the X coordinate of this <code>Rectangle2D</code> in
         * double precision.
         * @return the X coordinate of this <code>Rectangle2D</code>.
	 * @since 1.2
	 */
	public double getX() {
	    return x;
	}

	/**
	 * Returns the Y coordinate of this <code>Rectangle2D</code> in
         * double precision.
         * @return the Y coordinate of this <code>Rectangle2D</code>.
	 * @since 1.2
	 */
	public double getY() {
	    return y;
	}

	/**
	 * Returns the width of this <code>Rectangle2D</code> in 
         * double precision.
         * @return the width of this <code>Rectangle2D</code>.
	 * @since 1.2
	 */
	public double getWidth() {
	    return width;
	}

	/**
	 * Returns the height of this <code>Rectangle2D</code> in 
         * double precision.
         * @return the height of this <code>Rectangle2D</code>.
	 * @since 1.2
	 */
	public double getHeight() {
	    return height;
	}

	/**
	 * Determines whether or not this <code>Rectangle2D</code> 
         * is empty.
         * @return <code>true</code> if this <code>Rectangle2D</code>
         * is empty; <code>false</code> otherwise.
	 * @since 1.2
	 */
	public boolean isEmpty() {
	    return (width <= 0.0) || (height <= 0.0);
	}

	/**
	 * Sets the location and size of this <code>Rectangle2D</code>
         * to the specified double values.
         * @param x,&nbsp;y the coordinates to which to set the
         * upper left corner of this <code>Rectangle2D</code>
         * @param w the value to use to set the width of this
         * <code>Rectangle2D</code>
         * @param h the value to use to set the height of this
         * <code>Rectangle2D</code>
	 * @since 1.2
	 */
	public void setRect(double x, double y, double w, double h) {
	    this.x = x;
	    this.y = y;
	    this.width = w;
	    this.height = h;
	}

	/**
	 * Sets this <code>Rectangle2D</code> to be the same as the
         * specified <code>Rectangle2D</code>.
         * @param r the specified <code>Rectangle2D</code>
	 * @since 1.2
	 */
	public void setRect(Rectangle2D r) {
	    this.x = r.getX();
	    this.y = r.getY();
	    this.width = r.getWidth();
	    this.height = r.getHeight();
	}

	/**
	 * Determines where the specified double coordinates lie with respect
	 * to this <code>Rectangle2D</code>.
         * This method computes a binary OR of the appropriate mask values
         * indicating, for each side of this <code>Rectangle2D</code>,
         * whether or not the specified coordinates are on the same side
         * of the edge as the rest of this <code>Rectangle2D</code>.
         * @param x,&nbsp;y the specified coordinates
         * @return the logical OR of all appropriate out codes.	
         * @see Rectangle2D#OUT_LEFT
	 * @see Rectangle2D#OUT_TOP
	 * @see Rectangle2D#OUT_RIGHT
	 * @see Rectangle2D#OUT_BOTTOM
	 * @since 1.2
	 */
	public int outcode(double x, double y) {
	    int out = 0;
	    if (this.width <= 0) {
		out |= OUT_LEFT | OUT_RIGHT;
	    } else if (x < this.x) {
		out |= OUT_LEFT;
	    } else if (x > this.x + this.width) {
		out |= OUT_RIGHT;
	    }
	    if (this.height <= 0) {
		out |= OUT_TOP | OUT_BOTTOM;
	    } else if (y < this.y) {
		out |= OUT_TOP;
	    } else if (y > this.y + this.height) {
		out |= OUT_BOTTOM;
	    }
	    return out;
	}

	/**
	 * Returns the high precision bounding box of this
         * <code>Rectangle2D</code>.
         * @return the bounding box of this <code>Rectangle2D</code>.
	 * @since 1.2
	 */
	public Rectangle2D getBounds2D() {
	    return new Double(x, y, width, height);
	}

	/**
	 * Returns a new <code>Rectangle2D</code> object representing 
         * the intersection of this <code>Rectangle2D</code> with the
         * specified <code>Rectangle2D</code>.
	 * @param r the <code>Rectangle2D</code> to be intersected 
         * with this <code>Rectangle2D</code>
	 * @return the largest <code>Rectangle2D</code> contained in 
         * both the specified <code>Rectangle2D</code> and in this
         * <code>Rectangle2D</code>.
	 * @since 1.2
	 */
	public Rectangle2D createIntersection(Rectangle2D r) {
	    Rectangle2D dest = new Rectangle2D.Double();
	    Rectangle2D.intersect(this, r, dest);
	    return dest;
	}

	/**
	 * Returns a new <code>Rectangle2D</code> object representing 
         * the union of this <code>Rectangle2D</code> with the
         * specified <code>Rectangle2D</code>.
	 * @param r the <code>Rectangle2D</code> to be combined with
         * this <code>Rectangle2D</code>
	 * @return  the smallest <code>Rectangle2D</code> containing 
         * both the specified <code>Rectangle2D</code> and this 
         * <code>Rectangle2D</code>.
	 * @since 1.2
	 */
	public Rectangle2D createUnion(Rectangle2D r) {
	    Rectangle2D dest = new Rectangle2D.Double();
	    Rectangle2D.union(this, r, dest);
	    return dest;
	}

	/**
	 * Returns the <code>String</code> representation of this
         * <code>Rectangle2D</code>.
         * @return a <code>String</code> representing this 
         * <code>Rectangle2D</code>.
	 * @since 1.2
	 */
	public String toString() {
	    return getClass().getName()
		+ "[x=" + x +
		",y=" + y +
		",w=" + width +
		",h=" + height + "]";
	}
    }
