package org.jboss.bpmn2.editor.core.features.event;

import org.eclipse.bpmn2.Event;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.impl.AbstractLayoutFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.jboss.bpmn2.editor.core.features.ShapeUtil;

public class LayoutEventFeature extends AbstractLayoutFeature {

	public LayoutEventFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canLayout(ILayoutContext context) {
		PictogramElement pictoElem = context.getPictogramElement();
		if (!(pictoElem instanceof ContainerShape)) {
			return false;
		}
		EList<EObject> businessObjs = pictoElem.getLink().getBusinessObjects();
		return businessObjs.size() == 1 && businessObjs.get(0) instanceof Event;
	}

	@Override
	public boolean layout(ILayoutContext context) {
		boolean changed = false;

		ContainerShape containerShape = (ContainerShape) context.getPictogramElement();
		GraphicsAlgorithm containerGa = containerShape.getGraphicsAlgorithm();

		if (containerGa.getWidth() < ShapeUtil.EVENT_SIZE || containerGa.getWidth() > ShapeUtil.EVENT_SIZE) {
			containerGa.setWidth(ShapeUtil.EVENT_SIZE);
			changed = true;
		}

		int h = ShapeUtil.EVENT_SIZE + ShapeUtil.EVENT_TEXT_AREA;

		if (containerGa.getHeight() < h || containerGa.getHeight() > h) {
			containerGa.setHeight(ShapeUtil.EVENT_SIZE + ShapeUtil.EVENT_TEXT_AREA);
			changed = true;
		}

		return changed;
	}

}