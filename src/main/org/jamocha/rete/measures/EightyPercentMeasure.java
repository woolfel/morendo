package org.jamocha.rete.measures;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.Cube;
import org.jamocha.rete.CubeBinding;
import org.jamocha.rete.Index;
import org.jamocha.rete.Rete;

public class EightyPercentMeasure implements AggregateMeasure {

	public static final String EIGHTY_PERCENT = "80-percentile";
	
	public EightyPercentMeasure() {
	}

	protected BigDecimal calculate(Rete engine, Cube cube, List data) {
		if (data != null) {
			java.util.Collections.sort(data);
			Object value = data.get( (int)(data.size() * .8) );
			if (value instanceof BigDecimal) {
				return (BigDecimal)value;
			} else if (value instanceof Number) {
				return new BigDecimal( ((Number)value).doubleValue() );
			} else {
				return new BigDecimal( value.toString() );
			}
		}
		return new BigDecimal(0);
	}

	public BigDecimal calculate(Rete engine, Cube cube, Object[] data, CubeBinding binding) {
		if (data != null) {
			ArrayList values = new ArrayList();
			for (int idx=0; idx < data.length; idx++) {
				Index facts = (Index)data[idx];
				Object value = facts.getFacts()[binding.getLeftRow()].getSlotValue(binding.getLeftIndex());
				values.add(value);
			}
			return this.calculate(engine, cube, values);
		} else {
			return new BigDecimal(0);
		}
	}

	public String getDescription() {
		return "80th percentile of a given set of numbers";
	}

	public String getMeasureName() {
		return EIGHTY_PERCENT;
	}

}
