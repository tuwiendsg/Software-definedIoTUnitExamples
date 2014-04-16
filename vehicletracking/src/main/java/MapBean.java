import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.component.gmap.GMap;
import org.primefaces.event.map.MarkerDragEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import org.primefaces.push.PushContext;
import org.primefaces.push.PushContextFactory;

@ManagedBean(name = "mapBean")
@ApplicationScoped
public class MapBean implements Serializable {

	private MapModel draggableModel;
	private String label = "Not tracking any vehicle!";
	private PushContext pushContext;
	private String lat = "0";
	private String lon = "0";

	public MapBean() {
		draggableModel = new DefaultMapModel();
		this.pushContext = PushContextFactory.getDefault().getPushContext();
		// Shared coordinates
		LatLng coord1 = new LatLng(36.879466, 30.667648);
		// Draggable
		draggableModel.addOverlay(new Marker(coord1, "Konyaalti"));
		for (Marker marker : draggableModel.getMarkers()) {
			marker.setDraggable(true);
		}
	}

	public void click(ActionEvent actionEvent) {

		GMap gmap = (GMap) FacesContext.getCurrentInstance().getViewRoot().findComponent("form:map");
		gmap.getModel().getMarkers().get(0).setLatlng(new LatLng(Double.valueOf(this.lat), Double.valueOf(this.lon)));
	}

	public void ajaxListener() {
		System.out.println("ajax listener called!");
	}


	public MapModel getDraggableModel() {
		return draggableModel;
	}
	public void onMarkerDrag(MarkerDragEvent event) {
		Marker marker = event.getMarker();

		addMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, "Marker Dragged", "Lat:" + marker.getLatlng().getLat() + ", Lng:" + marker.getLatlng().getLng()));
	}

	public void addMessage(FacesMessage message) {
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLat() {
		return lat;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getLon() {
		return lon;
	}
}