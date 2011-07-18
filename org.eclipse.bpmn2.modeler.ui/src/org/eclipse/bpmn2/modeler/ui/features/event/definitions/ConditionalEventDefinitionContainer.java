/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 *  All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.ui.features.event.definitions;

import org.eclipse.bpmn2.ConditionalEventDefinition;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.bpmn2.modeler.core.ModelHandler;
import org.eclipse.bpmn2.modeler.core.features.event.definitions.CreateEventDefinition;
import org.eclipse.bpmn2.modeler.core.features.event.definitions.DecorationAlgorithm;
import org.eclipse.bpmn2.modeler.core.features.event.definitions.EventDefinitionFeatureContainer;
import org.eclipse.bpmn2.modeler.core.utils.GraphicsUtil;
import org.eclipse.bpmn2.modeler.ui.ImageProvider;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

public class ConditionalEventDefinitionContainer extends EventDefinitionFeatureContainer {

	@Override
	public boolean canApplyTo(Object o) {
		return super.canApplyTo(o) && o instanceof ConditionalEventDefinition;
	}

	@Override
	public ICreateFeature getCreateFeature(IFeatureProvider fp) {
		return new CreateConditionalEventDefinition(fp);
	}

	@Override
	protected Shape drawForStart(DecorationAlgorithm algorithm, ContainerShape shape) {
		return draw(shape);
	}

	@Override
	protected Shape drawForEnd(DecorationAlgorithm algorithm, ContainerShape shape) {
		return draw(shape);
	}

	@Override
	protected Shape drawForThrow(DecorationAlgorithm decorationAlgorithm, ContainerShape shape) {
		return null; // NOT ALLOWED ACCORDING TO SPEC
	}

	@Override
	protected Shape drawForCatch(DecorationAlgorithm decorationAlgorithm, ContainerShape shape) {
		return draw(shape);
	}

	@Override
	protected Shape drawForBoundary(DecorationAlgorithm algorithm, ContainerShape shape) {
		return draw(shape);
	}

	private Shape draw(ContainerShape shape) {
		Shape conditionShape = Graphiti.getPeService().createShape(shape, false);
		GraphicsUtil.createEventImage(conditionShape, ImageProvider.IMG_20_CONDITION);
		return conditionShape;
	}

	public static class CreateConditionalEventDefinition extends CreateEventDefinition {

		@Override
		public boolean canCreate(ICreateContext context) {
			if (!super.canCreate(context)) {
				return false;
			}

			Event e = (Event) getBusinessObjectForPictogramElement(context.getTargetContainer());
			if (e instanceof ThrowEvent) {
				return false;
			}

			return true;
		}

		public CreateConditionalEventDefinition(IFeatureProvider fp) {
			super(fp, "Conditional Event Definition", "Conditional trigger");
		}

		@Override
		protected EventDefinition createEventDefinition(ICreateContext context) {
			return ModelHandler.FACTORY.createConditionalEventDefinition();
		}

		@Override
		protected String getStencilImageId() {
			return ImageProvider.IMG_16_CONDITION;
		}
	}
}