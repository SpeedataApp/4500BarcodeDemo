package com.android.barcode;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ToggleButton;

public class BarcodeDemoActivity extends Activity implements OnClickListener, OnTouchListener {
	
	private EditText mReception;
	//���ܹ㲥
	private String RECE_DATA_ACTION = "com.se4500.onDecodeComplete";
	//����ɨ��㲥
	private String START_SCAN_ACTION = "com.geomobile.se4500barcode";
	
	private String STOP_SCAN="com.geomobile.se4500barcode.poweroff";
	
	//ֹͣɨ��㲥,�˹㲥ʵ��ֹͣɨ��
	private String STOP_SCAN_ACTION = "com.geomobile.se4500barcodestop";
	
	//��Ӳ�����btnTest����onTouchListener��ʵ�ֹ���
	private Button btnSingleScan, btnClear, btnTest;
	private ToggleButton toggleButtonRepeat;
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(android.content.Context context,
				android.content.Intent intent) {
			String action = intent.getAction();
			if (action.equals(RECE_DATA_ACTION)) {
				String data = intent.getStringExtra("se4500");
				mReception.append(data+"\n");
				if (isRepeat) {
					cancelRepeat();
					repeatScan();
				}
			}
		}

	};

	public class MyTask extends TimerTask {
		@Override
		public void run() {
			startScan();
		}
	}
	private Timer timer = new Timer();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.barcode);
		judgePropert();
		btnSingleScan = (Button) findViewById(R.id.buttonscan);
		btnClear = (Button) findViewById(R.id.buttonclear);
		btnTest = (Button) findViewById(R.id.buttontest);
		toggleButtonRepeat = (ToggleButton) findViewById(R.id.button_repeat);
		btnSingleScan.setOnClickListener(this);
		btnClear.setOnClickListener(this);
		btnTest.setOnTouchListener(this);
		// toggleButtonRepeat.setOnClickListener(this);
		toggleButtonRepeat
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							isRepeat = true;
							repeatScan();
						} else {
							isRepeat = false;
							cancelRepeat();
						}
					}
				});

		mReception = (EditText) findViewById(R.id.EditTextReception);
		IntentFilter iFilter = new IntentFilter();
		
		//ע��ϵͳ�㲥  ����ɨ�赽������
		iFilter.addAction(RECE_DATA_ACTION);
		registerReceiver(receiver, iFilter);
	}

	/**
	 * �жϿ��ɨ���Ƿ�ѡ   ����ѡ��ת��ϵͳ�����н�������
	 */
	private void judgePropert() {
		String result = SystemProperties.get("persist.sys.keyreport", "true");
		if (result.equals("false")) {
			new AlertDialog.Builder(this)
					.setTitle(R.string.key_test_back_title)
					.setMessage(R.string.action_dialog_setting_config)
					.setPositiveButton(
							R.string.action_dialog_setting_config_sure_go,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									Intent intent = new Intent(
											Settings.ACTION_ACCESSIBILITY_SETTINGS);
									startActivityForResult(intent, 1);
								}
							})
					.setNegativeButton(R.string.action_exit_cancel,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									finish();
								}

							}

					).show();
		}
	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Intent intent =new Intent();
		intent.setAction("com.geomobile.se4500barcode.poweroff");
		sendBroadcast(intent);
		Intent intent2 =new Intent();
		intent2.setAction(STOP_SCAN_ACTION);
		sendBroadcast(intent2);
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub

		super.onPause();
		if (isRepeat) {
			cancelRepeat();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if (isRepeat) {
			repeatScan();
		}
		super.onResume();
	}

	private boolean isRepeat = false;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.buttonclear:
			mReception.setText("");
			break;
		case R.id.buttonscan:
			startScan();
			break;
		default:
			break;
		}
	}

	/**
	 * ���͹㲥  ����ϵͳɨ��
	 */
	private void startScan() {
		Intent intent = new Intent();
		intent.setAction(START_SCAN_ACTION);
		sendBroadcast(intent, null);
	}
	

	private void repeatScan() {
		if (timer == null) {
			timer = new Timer();
		}
		timer.scheduleAtFixedRate(new MyTask(), 100, 4 * 1000);
	}

	private void cancelRepeat() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.buttontest){
			switch (event.getAction()) {
			
			case MotionEvent.ACTION_UP:{
				Intent intent = new Intent();
				intent.setAction(STOP_SCAN_ACTION);
				sendBroadcast(intent, null);
				break;
			}
			case MotionEvent.ACTION_DOWN:{
				Intent intent = new Intent();
				intent.setAction(START_SCAN_ACTION);
				sendBroadcast(intent, null);
				break;
			}
			
			default:
				break;
			}
		}
		return false;
	};
}
