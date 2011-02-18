package org.jboss.bpmn2.editor.core.features.task;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.jboss.bpmn2.editor.core.ImageProvider;

public class AddBusinessRuleTaskFeature extends AddTaskFeature {

	public AddBusinessRuleTaskFeature(IFeatureProvider fp) {
	    super(fp);
    }
	
	@Override
	protected int getWidth() {
	    return SizeConstants.WIDTH + 50;
	}
	
	@Override
	protected void decorateTask(RoundedRectangle rect, IAddContext context) {
		IGaService service = Graphiti.getGaService();
		Image img = service.createImage(rect, ImageProvider.IMG_16_BUSINESS_RULE_TASK);
		service.setLocationAndSize(img, 0, 0, 16, 16);
	}
}