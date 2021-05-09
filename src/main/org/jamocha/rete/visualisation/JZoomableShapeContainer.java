/*
 * Copyright 2002-2007 Jamocha Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://jamocha.sourceforge.net/ Unless required by applicable law or
 * agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package org.jamocha.rete.visualisation;

import java.awt.Graphics2D;
import java.util.Iterator;

/**
 * @author Josef Alexander Hahn
 * A special JShapeContainer which can zoom.
 */
public class JZoomableShapeContainer extends JShapeContainer {

	protected JMiniRadarShapeContainer radarShapeContainer;
	int zoomLevel;
	
	public JZoomableShapeContainer() {
		super();
		zoomLevel=0;
	}
	
	/**
	 * increases the zoom level
	 */
	public void zoomIn() {
		zoomLevel++;
		repaint();
		if (radarShapeContainer!=null) {
			radarShapeContainer.componentResized(null);
			radarShapeContainer.repaint();
		}
	}

	/**
	 * decreases the zoom level
	 */
	public void zoomOut() {
		zoomLevel--;
		repaint();
		if (radarShapeContainer!=null) {
			radarShapeContainer.componentResized(null);
			radarShapeContainer.repaint();
		}
	}

	/**
	 * @return the actual zoom factor
	 */
	public double getZoomFactor() {
		double factor=1.0;
		for (int i=1; i<=zoomLevel; i++) {
			factor*=2.0;
		}
		for (int i=-1; i>=zoomLevel; i--) {
			factor/=2.0;
		}
		return factor;
	}
	
	protected void drawPrimitive(Primitive p,Graphics2D g) {
		double factor=getZoomFactor();
		p.draw(g,offsetX,offsetY,factor, factor);
	}
	


	public JMiniRadarShapeContainer getRadarShapeContainer() {
		return radarShapeContainer;
	}

	/**
	 * sets the corresponding mini-map
	 * @param radarShapeContainer mini-map
	 */
	public void setRadarShapeContainer(JMiniRadarShapeContainer radarShapeContainer) {
		this.radarShapeContainer = radarShapeContainer;
	}
	
	/**
	 * Returns the Shape at the given position
	 * @param x x-coordinate of the absolute position
	 * @param y y-coordinate of the absolute position
	 * @return the shape at that position
	 */
	public Shape getShapeAtPosition(int x, int y) {
		int rx=(int)Math.round(x/getZoomFactor()+offsetX);
		int ry=(int)Math.round(y/getZoomFactor()+offsetY);
		for(Iterator<Shape> it=shapes.iterator();it.hasNext();){
			Shape s=it.next();
			int offX=(rx-s.getX());
			int offY=(ry-s.getY());
			if (offX>=0 && offY>=0 && offX<=s.getWidth() && offY<=s.getHeight())
				return s;
		}
		return null;
	}

}
