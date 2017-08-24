package com.bymarcin.openglasses.surface;

import com.bymarcin.openglasses.surface.widgets.component.face.*;
import com.bymarcin.openglasses.surface.widgets.component.world.*;

public enum WidgetType {
	SQUARE(SquareWidget.class),
	TRIANGLE(TriangleWidget.class),
	DOT(Dot.class),
	TEXT(Text.class),
	CUBE3D(Cube3D.class),
	FLOATINGTEXT(FloatingText.class),
	TRIANGLE3D(Triangle3D.class),
	QUAD3D(Quad3D.class),
	DOT3D(Dot3D.class),
	LINE3D(Line3D.class),
	QUAD(Quad.class),
	ITEMICON(ItemIcon.class)
	;
	
	Class<? extends Widget> clazz;
	private WidgetType(Class<? extends Widget> cl) {
		clazz = cl;
	}
	
	public Widget getNewInstance(){
		try {
			return this.clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
