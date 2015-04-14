package com.practice.android.demo.drawer.fragments;

import android.app.Fragment;
import android.content.Context;
import android.hardware.*;
import android.os.Bundle;
import android.text.Html;
import android.view.*;
import android.widget.TextView;
import com.practice.android.demo.drawer.activities.MainActivity;
import com.practice.android.demo.drawer.activities.R;

import java.util.List;

public class SensorFragment extends Fragment implements SensorEventListener{
	private MainActivity  activity;
	private Sensor        sensor;
	private SensorManager managerSensor;
	private long          lastUpdate;
	private TextView      textOutput;
	private float         cacheX, cacheY, cacheZ;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View viewParent= (View)inflater.inflate(R.layout.fragment_sensor, container, false);
		TextView textSensors= (TextView)viewParent.findViewById(R.id.text_sensor_list);
		textOutput 	= (TextView)viewParent.findViewById(R.id.text_sensor_output);
		activity 	= (MainActivity)getActivity();
		managerSensor = (SensorManager)activity.getSystemService(Context.SENSOR_SERVICE);

		List<Sensor> sensors = managerSensor.getSensorList(Sensor.TYPE_ALL);

		for(Sensor sensor : sensors){
			textSensors.append(sensor.getVendor() + " - ");
			textSensors.append(sensor.getVersion() + ", ");
			textSensors.append(sensor.getName() + "\n");
		}

		lastUpdate 	= 0;
		sensor 		= managerSensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		return viewParent;
	}

	@Override
	public void onResume(){
		super.onResume();
		managerSensor.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public void onPause(){
		super.onPause();
		managerSensor.unregisterListener(this);
	}

	@Override
	public void onSensorChanged(SensorEvent event){
		float x= event.values[0],
			y = event.values[1],
			z = event.values[2];
		long  currentTime = System.currentTimeMillis();

		if(currentTime - lastUpdate > 500){
			textOutput.setText(Html.fromHtml(
				"x= <b><u><i>"+
				x+"</i></u></b>,  y= <b><u><i>"+
				y+"</i></u></b>,  z= <b><u><i>"+
				z+"</i></u></b>"));

			lastUpdate= currentTime;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy){
	}
}
