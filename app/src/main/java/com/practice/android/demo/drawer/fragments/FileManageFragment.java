package com.practice.android.demo.drawer.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.practice.android.demo.drawer.activities.R;
import com.practice.android.demo.drawer.utils.Common;
import com.practice.android.demo.drawer.utils.Validation;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManageFragment extends Fragment{
	private EditText fieldFilename, 	fieldContent;
	private ImageButton buttonAction,	buttonDelete;
	private ListView 	viewList;
	private Activity 	activity;

	private FileListAdapter adapter;

	private Validation 	validation;
	private File 		parentDir;

	private StringBuffer bufferCache;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View viewParent	= inflater.inflate(R.layout.fragment_file_manage, container, false);

		activity		= getActivity();
		fieldFilename 	= (EditText) viewParent.findViewById(R.id.field_file_name);
		fieldContent	= (EditText) viewParent.findViewById(R.id.field_file_content);
		buttonAction 	= (ImageButton) viewParent.findViewById(R.id.button_file_action);
		buttonDelete	= (ImageButton) viewParent.findViewById(R.id.button_file_delete);
		viewList		= (ListView) viewParent.findViewById(R.id.view_list);
		adapter			= new FileListAdapter();
		validation		= Validation.instance().setContext(activity);
		parentDir		= getParentDir();
		bufferCache 	= new StringBuffer();

		postCreateView();

		return viewParent;
	}

	private void postCreateView(){
		adapter.scanParentDir(parentDir);

		buttonAction.setEnabled(false);
		buttonAction.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				if(validation.fieldCheck(fieldFilename, Validation.Check.REQUIRE))
					requestWrite();
			}
		});

		buttonDelete.setEnabled(false);
		buttonDelete.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				int position= viewList.getCheckedItemPosition();

				if(position!= ListView.INVALID_POSITION){
					adapter.removeFileWithUnchecked(position);
					fieldFilename.getText().clear();
					fieldContent.getText().clear();
				}
			}
		});

		fieldFilename.addTextChangedListener(new TextWatcher(){
			public void beforeTextChanged(CharSequence s, int start, int count, int after){
				String filename= s.toString().trim();

				int position= adapter.getPosition(filename);
				if(position!= ListView.INVALID_POSITION)
					viewList.setItemChecked(position, false);
			}

			public void onTextChanged(CharSequence s, int start, int before, int count){}
			public void afterTextChanged(Editable s){
				String filename = s.toString().trim();

				if(filename.isEmpty()){
					buttonAction.setEnabled(false);
					buttonDelete.setEnabled(false);
				}else{
					buttonAction.setEnabled(true);

					int position= adapter.getPosition(filename);

					if(position!= ListView.INVALID_POSITION){
						viewList.setItemChecked(position, true);
						readFile(adapter.getItem(position));
						buttonDelete.setEnabled(true);
						buttonAction.setImageDrawable(activity.getResources().getDrawable(R.drawable.file_modify));
					}else{
						fieldContent.getText().clear();
						buttonDelete.setEnabled(false);
						buttonAction.setImageDrawable(activity.getResources().getDrawable(R.drawable.file_add));
					}
				}
			}
		});
	}

	private File getParentDir(){
		File file;

		file= new File(hasValidExternalStorage()?
			Environment.getExternalStorageDirectory():
			activity.getFilesDir(), "DemoDrawer");

		if(!file.exists())
			file.mkdir();

		return file;
	}

	private boolean hasValidExternalStorage(){
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	private void refreshList(){
		viewList.clearChoices();
		viewList.requestLayout();
	}

	private void requestWrite(){
		String filename= Common.fieldTextTrimmed(fieldFilename);

		File file;

		int position= adapter.getPosition(filename);

		if(position!= ListView.INVALID_POSITION)
			file= adapter.getItem(position);
		else{
			file= new File(parentDir, filename);

			try{
				if(!file.exists())
					file.createNewFile();

				adapter.addFileWithChecked(file);
			}catch(IOException e){ Common.toastShort(activity, e.getLocalizedMessage()); }
		}

		if(!bufferCache.toString().equals(fieldContent.getText().toString()))
			writeFile(file);
	}

	private void readFile(File file){
		try(DataInputStream stream= new DataInputStream(new FileInputStream(file))){

			fieldContent.getText().clear();
			bufferCache.setLength(0);

			String content= stream.readUTF();

			fieldContent.setText(content);
			bufferCache.append(content);
		}catch(Exception e){ Common.toastShort(activity, e.getLocalizedMessage()); }
	}

	private void writeFile(File file){
		try(DataOutputStream stream= new DataOutputStream(new FileOutputStream(file))){
			String content= fieldContent.getText().toString();

			stream.writeUTF(content);
			stream.flush();

			bufferCache.setLength(0);
			bufferCache.append(content);
		}catch(Exception e){ Common.toastShort(activity, e.getLocalizedMessage()); }
	}

	private class FileListAdapter extends BaseAdapter{
		private final List<File> files;

		public FileListAdapter(){
			files= new ArrayList<>();

			viewList.setAdapter(this);
		}

		public void scanParentDir(File parentDir){
			files.clear();

			for(File file: parentDir.listFiles())
				files.add(file);

			notifyDataSetChanged();
		}

		public void addFileWithChecked(File file){
			files.add(file);
			notifyDataSetChanged();

			viewList.setItemChecked(getCount()- 1, true);
			buttonDelete.setEnabled(true);
		}

		public void removeFileWithUnchecked(int position){
			File file= files.remove(position);
			file.delete();

			notifyDataSetChanged();

			refreshList();
			buttonDelete.setEnabled(false);
		}

		public int getPosition(String filename){
			for(int i= 0; i< files.size(); i++)
				if(files.get(i).getName().equals(filename))
					return i;

			return ListView.INVALID_POSITION;
		}

		public int getCount(){ return files.size(); }
		public File getItem(int position){ return files.get(position); }
		public long getItemId(int position){ return position; }

		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			final CheckedTextView viewNode= (CheckedTextView) activity.getLayoutInflater().inflate(R.layout.list_file_item, parent, false);

			viewNode.setText(getItem(position).getName());
			viewNode.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v){
					if(viewNode.isChecked()){
						refreshList();
						fieldFilename.getText().clear();
						fieldContent.getText().clear();
					}else
						fieldFilename.setText(getItem(position).getName());
				}
			});

			return viewNode;
		}
	}
}
