package org.jboss.bpmn2.editor.core.features.event;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.CancelEventDefinition;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.features.impl.AbstractMoveShapeFeature;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.Property;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.styles.AdaptedGradientColoredAreas;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.ChopboxAnchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeService;
import org.eclipse.graphiti.util.PredefinedColoredAreas;
import org.jboss.bpmn2.editor.core.Activator;
import org.jboss.bpmn2.editor.core.ImageProvider;
import org.jboss.bpmn2.editor.core.ModelHandler;
import org.jboss.bpmn2.editor.core.features.FeatureContainer;
import org.jboss.bpmn2.editor.core.features.FeatureSupport;
import org.jboss.bpmn2.editor.core.features.ShapeUtil;
import org.jboss.bpmn2.editor.core.features.StyleUtil;
import org.jboss.bpmn2.editor.core.features.task.SizeConstants;

public class BoundaryEventFeatureContainer implements FeatureContainer {

	private static String cancelKey = "cancel-activity";

	@Override
	public boolean canApplyTo(BaseElement element) {
		return element instanceof BoundaryEvent;
	}

	@Override
	public ICreateFeature getCreateFeature(IFeatureProvider fp) {
		return new CreateBoundaryEvent(fp);
	}

	@Override
	public IAddFeature getAddFeature(IFeatureProvider fp) {
		return new AddBoundaryEvent(fp);
	}

	@Override
	public IUpdateFeature getUpdateFeature(IFeatureProvider fp) {
		return new AbstractUpdateFeature(fp) {

			@Override
			public IReason updateNeeded(IUpdateContext context) {
				Property cancelProperty = Graphiti.getPeService().getProperty(context.getPictogramElement(), cancelKey);
				BoundaryEvent event = (BoundaryEvent) getBusinessObjectForPictogramElement(context
				        .getPictogramElement());
				boolean changed = Boolean.parseBoolean(cancelProperty.getValue()) != event.isCancelActivity();
				IReason reason = changed ? Reason.createTrueReason("Boundary type changed") : Reason
				        .createFalseReason();
				return reason;
			}

			@Override
			public boolean update(IUpdateContext context) {
				BoundaryEvent event = (BoundaryEvent) getBusinessObjectForPictogramElement(context
				        .getPictogramElement());

				boolean canUpdate = true;

				List<EventDefinition> definitions = event.getEventDefinitions();

				if (event.isCancelActivity() == false) {
					for (EventDefinition d : definitions) {
						if (d instanceof ErrorEventDefinition || d instanceof CancelEventDefinition
						        || d instanceof CompensateEventDefinition) {
							canUpdate = false;
							break;
						}
					}
				}

				if (canUpdate == false)
					return false;

				Graphiti.getPeService().setPropertyValue(context.getPictogramElement(), cancelKey,
				        Boolean.toString(event.isCancelActivity()));

				Ellipse ellipse = (Ellipse) context.getPictogramElement().getGraphicsAlgorithm();
				Ellipse innerEllipse = (Ellipse) ellipse.getGraphicsAlgorithmChildren().get(0);
				LineStyle lineStyle = event.isCancelActivity() ? LineStyle.SOLID : LineStyle.DASH;

				ellipse.setLineStyle(lineStyle);
				innerEllipse.setLineStyle(lineStyle);

				return true;
			}

			@Override
			public boolean canUpdate(IUpdateContext context) {
				return getBusinessObjectForPictogramElement(context.getPictogramElement()) instanceof BoundaryEvent;
			}
		};
	}

	public static class CreateBoundaryEvent extends AbstractCreateFeature {

		protected FeatureSupport support = new FeatureSupport() {
			@Override
			public Object getBusinessObject(PictogramElement element) {
				return getBusinessObjectForPictogramElement(element);
			}
		};

		public CreateBoundaryEvent(IFeatureProvider fp) {
			super(fp, "Boundary Event", "Adds boundary event to activity, defaults to interrupting");
		}

		@Override
		public boolean canCreate(ICreateContext context) {
			Object o = getBusinessObjectForPictogramElement(context.getTargetContainer());
			return o != null && o instanceof Activity;
		}

		@Override
		public Object[] create(ICreateContext context) {
			BoundaryEvent event = null;
			try {
				Activity activity = (Activity) getBusinessObjectForPictogramElement(context.getTargetContainer());
				ModelHandler handler = support.getModelHanderInstance(getDiagram());
				event = ModelHandler.FACTORY.createBoundaryEvent();
				event.setAttachedToRef(activity);
				event.setName("Boundary event");
				event.setCancelActivity(true); // by default is interrupting
				handler.addFlowElement(support.getTargetParticipant(context, handler), event);
			} catch (IOException e) {
				Activator.logError(e);
			}
			addGraphicalRepresentation(context, event);
			return new Object[] { event };
		}

		@Override
		public String getCreateImageId() {
			return ImageProvider.IMG_16_BOUNDARY_EVENT;
		}

		@Override
		public String getCreateLargeImageId() {
			return getCreateImageId(); // FIXME
		}
	}

	private class AddBoundaryEvent extends AbstractAddShapeFeature {

		public AddBoundaryEvent(IFeatureProvider fp) {
			super(fp);
		}

		@Override
		public boolean canAdd(IAddContext context) {
			if (!(context.getNewObject() instanceof BoundaryEvent))
				return false;
			Object bo = getBusinessObjectForPictogramElement(context.getTargetContainer());
			return bo != null && bo instanceof Activity;
		}

		@Override
		public PictogramElement add(IAddContext context) {
			IPeService peService = Graphiti.getPeService();
			IGaService gaService = Graphiti.getGaService();

			Collection<Shape> shapes = peService.getAllContainedShapes(context.getTargetContainer());
			GraphicsAlgorithm ga = shapes.iterator().next().getGraphicsAlgorithm();

			BoundaryEvent event = (BoundaryEvent) context.getNewObject();

			ContainerShape containerShape = peService.createContainerShape(context.getTargetContainer(), true);

			Ellipse ellipse = gaService.createEllipse(containerShape);
			gaService.setLocationAndSize(ellipse, ga.getWidth(), ga.getHeight() - 18, ShapeUtil.EVENT_SIZE,
			        ShapeUtil.EVENT_SIZE);

			Style style = StyleUtil.getStyleForClass(getDiagram());
			ellipse.setStyle(style);

			AdaptedGradientColoredAreas gradient = PredefinedColoredAreas.getBlueWhiteAdaptions();
			gaService.setRenderingStyle(ellipse, gradient);

			Ellipse circle = ShapeUtil.createIntermediateEventCircle(ellipse);
			circle.setStyle(style);

			link(containerShape, event);

			ChopboxAnchor anchor = peService.createChopboxAnchor(containerShape);
			anchor.setReferencedGraphicsAlgorithm(ellipse);

			GraphicsAlgorithm parentGa = context.getTargetContainer().getGraphicsAlgorithm();
			gaService.setSize(parentGa, parentGa.getWidth() + SizeConstants.PADDING_RIGHT, parentGa.getHeight());
			gaService.setSize(ga, ga.getWidth() + SizeConstants.PADDING_RIGHT, ga.getHeight());

			if (event.eResource() == null) {
				getDiagram().eResource().getContents().add(event);
			}

			peService.setPropertyValue(containerShape, cancelKey, "true");
			return containerShape;
		}
	}

	@Override
	public IDirectEditingFeature getDirectEditingFeature(IFeatureProvider fp) {
		return null;
	}

	@Override
	public ILayoutFeature getLayoutFeature(IFeatureProvider fp) {
		return null;
	}

	@Override
	public IMoveShapeFeature getMoveFeature(IFeatureProvider fp) {
		return new AbstractMoveShapeFeature(fp) {

			@Override
			public void moveShape(IMoveShapeContext context) {
			}

			@Override
			public boolean canMoveShape(IMoveShapeContext context) {
				return false;
			}
		};
	}

	@Override
	public IResizeShapeFeature getResizeFeature(IFeatureProvider fp) {
		return new DefaultResizeShapeFeature(fp) {
			@Override
			public boolean canResizeShape(IResizeShapeContext context) {
			    return false;
			}
		};
	}
}