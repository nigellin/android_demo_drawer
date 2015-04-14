package com.practice.android.demo.drawer.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.practice.android.demo.drawer.activities.R;
import com.practice.android.demo.drawer.views.CanvasView;

import java.util.Arrays;
import java.util.List;

public class CanvasFragment extends Fragment{
	private CanvasView viewCanvas;
	private Spinner    spinner;
	private SeekBar    seekbar;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View viewParent = inflater.inflate(R.layout.fragment_canvas, container, false);

		viewCanvas  = (CanvasView)viewParent.findViewById(R.id.view_canvas);
		spinner     = (Spinner)viewParent.findViewById(R.id.spinner_colors);
		seekbar     = (SeekBar)viewParent.findViewById(R.id.seekbar_stroke_width);

		initial();

		return viewParent;
	}

	private void initial(){
		seekbar.setMax(35);
		seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
			public void onStartTrackingTouch(SeekBar seekBar){}
			public void onStopTrackingTouch(SeekBar seekBar){}
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				viewCanvas.setStrokeWidth(progress + 5);
			}
		});

		final ColorAdapter adapter = new ColorAdapter();

		spinner.setAdapter(adapter);
		seekbar.setProgress(10);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
			public void onNothingSelected(AdapterView<?> parent){}
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
				viewCanvas.setStrokeColor(adapter.getItem(position).getColor());
			}
		});
	}

	public void doCanvasClear(){
		viewCanvas.clear();
	}

	@Override
	public void onStop(){
		super.onStop();
		viewCanvas.stop();
	}

	private class ColorAdapter extends BaseAdapter{
		private List<ColorStyle> styles = Arrays.asList(ColorStyle.values());

		@Override
		public int getCount(){
			return styles.size();
		}
		@Override
		public ColorStyle getItem(int position){
			return styles.get(position);
		}
		@Override
		public long getItemId(int position){
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			TextView viewText = (TextView) getActivity().getLayoutInflater().inflate(R.layout.support_simple_spinner_dropdown_item, parent, false);
			ColorStyle style = styles.get(position);
			viewText.setText(style.name());
			viewText.setTextColor(style.getColor());
			viewText.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
			return viewText;
		}
	}

	enum ColorStyle{
		BLACK(Color.BLACK), GRAY(Color.GRAY), RED(Color.RED), BLUE(Color.BLUE),
		GREEN(Color.GREEN), YELLOW(Color.YELLOW), CYAN(Color.CYAN);
		private int color;

		ColorStyle(int color){
			this.color=color;
		}

		public int getColor(){
			return color;
		}
	}
}
