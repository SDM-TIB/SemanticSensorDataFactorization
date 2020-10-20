import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.jena.rdf.model.Resource;

import com.google.gson.annotations.Expose;

public class observationClass {

	@Expose
	private final Resource sensor;
	@Expose
	private final Resource property;
	@Expose
	private final Resource phenomenon;
	@Expose
	private final Resource mURI;

	observationClass(final Resource sensor, final Resource property,
			final Resource phenomenon, final Resource mURI) {
		this.sensor = sensor;
		this.property = property;
		this.phenomenon = phenomenon;
		this.mURI = mURI;
	}

	public Resource getSensor() {
		return sensor;
	}

	public Resource getProperty() {
		return property;
	}

	public Resource getPhenomenon() {
		return phenomenon;
	}

	public Resource getmURI() {
		return mURI;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).append(sensor).append(property)
				.append(phenomenon).append(mURI).toHashCode();// two randomly
		// chosen prime
		// numbers
		// if deriving: appendSuper(super.hashCode()).

	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof observationClass)) { return false; }
		if (obj == this) { return true; }

		final observationClass o = (observationClass) obj;
		return new EqualsBuilder().append(sensor, o.sensor)
				.append(property, o.property).append(phenomenon, o.phenomenon)
				.append(mURI, o.mURI).isEquals();// if deriving:
													// appendSuper(super.equals(obj)).
	}

}
