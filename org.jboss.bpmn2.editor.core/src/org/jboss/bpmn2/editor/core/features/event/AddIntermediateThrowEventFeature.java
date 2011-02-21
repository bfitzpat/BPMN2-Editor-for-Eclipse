package org.jboss.bpmn2.editor.core.features.event;

import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.IntermediateThrowEvent;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.jboss.bpmn2.editor.core.features.ShapeUtil;
import org.jboss.bpmn2.editor.core.features.StyleUtil;

public class AddIntermediateThrowEventFeature extends AbstractAddEventFeature {

	public AddIntermediateThrowEventFeature(IFeatureProvider fp) {
	    super(fp);
    }

	@Override
    protected Class<? extends Event> getEventClass() {
	    return IntermediateThrowEvent.class;
    }
	

	@Override
	protected void decorateEllipse(Ellipse e) {
		Ellipse circle = ShapeUtil.createIntermediateEventCircle(e);
		circle.setForeground(manageColor(StyleUtil.CLASS_FOREGROUND));
	}
}