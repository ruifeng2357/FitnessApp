package com.damy.jiankang;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import com.damy.Utils.ResolutionSet;
import com.damy.common.*;

public class FileExplorer extends BaseActivity
{
    static final int MODE_OPEN = 0;
    static final int MODE_SAVE = 1;
    
    public static String 		szRetCode = "RET";
    
    static final String FILEEXPLORER_FILENAME = "FILEEXPLORER_FILENAME";

    private String m_szCurPath = "/";
    private int m_nDialogMode = MODE_OPEN;

    TextView txtTitle = null;
    ScrollView listScrollView = null;
    LinearLayout listScrollLayout = null;
    EditText txtFileName = null;
    Button btnOK = null;
    Button btnCancel = null;

    ArrayList<String> m_arrFileList = new ArrayList<String>();
    ArrayList<TextView> m_arrViewList = new ArrayList<TextView>();
    
    private int m_First = 0;

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            switch (which)
            {
                case DialogInterface.BUTTON_POSITIVE: //Yes button clicked
                {
                	/*
                    String szFileName = m_szCurPath + "/" + txtFileName.getText().toString();
                    Intent intent = new Intent(FileExplorer.this, Program.class);
                    JSONObject json_param = GlobalInstance.progInfo2Json(program_mode, prog_mode_infos);
                    if (json_param == null)
                        return;

                    if (saveFile(szFileName, json_param))
                        showAlert(FileExplorer.this, "File Browser", "Saving succeeded!");
                    else
                        showAlert(FileExplorer.this, "File Browser", "Saving failed!");
					
                    intent.putExtra(FileExplorer.PARAM_PROGRAM_PROGINFO, json_param.toString());
                    startActivity(intent);
                    finish();*/
                    break;
                }
                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    return;
            }
        }
    };


    private String getDefaultDir()
    {
        return Environment.getExternalStorageDirectory().getPath();
    }

    private void initVariables()
    {
    	/*
        m_szCurPath =getDefaultDir();
        m_nDialogMode = FileExplorer.this.getIntent().getIntExtra(FileExplorer.DIALOG_MODE_KEY, -1);

        String szJson = FileExplorer.this.getIntent().getStringExtra(FileExplorer.PARAM_PROGRAM_PROGINFO);

        try
        {
            JSONObject rootObj = new JSONObject(szJson);

            prog_mode_infos = new ArrayList<ProgramModeInfo>();

            int nProgMode = GlobalInstance.json2ProgInfo(rootObj, prog_mode_infos);
            if (nProgMode < 0)
            {
                showAlert(FileExplorer.this, "File Browser", "Failed to read file!");
                return;
            }

            program_mode = nProgMode;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        */
    }

    private void loadControls()
    {
        txtTitle = (TextView)findViewById(R.id.txtTitle);
        txtTitle.setTextColor(Color.WHITE);
        txtTitle.setBackgroundColor(Color.DKGRAY);

        listScrollView = (ScrollView)findViewById(R.id.filelist_scrollview);
        listScrollLayout = (LinearLayout)findViewById(R.id.filelist_layout);
        txtFileName = (EditText)findViewById(R.id.txt_filename);

        if (m_nDialogMode == MODE_OPEN)
        {
            txtFileName.setKeyListener(null);
            txtFileName.setFocusable(false);
        }
        
        m_szCurPath = getIntent().getStringExtra("CURFILEPATH");

        btnOK = (Button)findViewById(R.id.fileexplorer_btnok);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To change body of implemented methods use File | Settings | File Templates.
                if (m_nDialogMode == MODE_OPEN)
                {
                    if (!txtFileName.getText().toString().equals(""))
                    {
                        String szFileName = txtFileName.getText().toString();
                        /*
                        JSONObject json_param = openFile(szFileName);
                        if (json_param != null)
                        {
                        	
                            Intent intent = new Intent(FileExplorer.this, Program.class);
                            intent.putExtra(FileExplorer.PARAM_PROGRAM_PROGINFO, json_param.toString());
                            startActivity(intent);
                            finish();
                            
                        }
                        else
                            showAlert(FileExplorer.this, "File Browser", "Reading file failed!");
                        */
                        
                        //Intent intent = new Intent(FileExplorer.this, FeedbackActivity.class);
                        //intent.putExtra(FileExplorer.FILEEXPLORER_FILENAME, szFileName);
                        //startActivity(intent);
                        
                        Intent returnIntent = new Intent();
                		returnIntent.putExtra(szRetCode, szFileName);
                		setResult(RESULT_OK, returnIntent);
                		FileExplorer.this.finish();
                    }
                    else
                    {
                        showAlert(FileExplorer.this, "File Browser", "No files are selected!");
                        return;
                    }
                }
                else if (m_nDialogMode == MODE_SAVE)
                {
                	/*
                    if (!txtFileName.getText().toString().equals(""))
                    {
                        String szFileName = m_szCurPath + "/" + txtFileName.getText().toString();
                        for (int i = 0; i < m_arrFileList.size(); i++)
                        {
                            if (szFileName.equals(m_arrFileList.get(i)))
                            {
                                File file = new File(m_arrFileList.get(i));
                                if (file.isDirectory())
                                {
                                    showAlert(FileExplorer.this, "File Browser", "The directory of which name is same already exists!");
                                }
                                else if (file.isFile())
                                {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(FileExplorer.this);
                                    builder.setMessage("Overwrite existing file?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
                                }

                                return;
                            }
                        }

                        Intent intent = new Intent(FileExplorer.this, Program.class);
                        JSONObject json_param = GlobalInstance.progInfo2Json(program_mode, prog_mode_infos);
                        if (json_param == null)
                            return;

                        if (saveFile(szFileName, json_param))
                            showAlert(FileExplorer.this, "File Browser", "Saving succeeded!");
                        else
                            showAlert(FileExplorer.this, "File Browser", "Saving failed!");

                        intent.putExtra(FileExplorer.PARAM_PROGRAM_PROGINFO, json_param.toString());
                        startActivity(intent);
                        finish();
                        
                    }
                    else
                    {
                        showAlert(FileExplorer.this, "File Browser", "File name can not be empty!");
                        return;
                    }
                    */
                }
            }
        });


        btnCancel = (Button)findViewById(R.id.fileexplorer_btncancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To change body of implemented methods use File | Settings | File Templates.
            	/*
                Intent intent = new Intent(FileExplorer.this, Program.class);
                JSONObject json_param = GlobalInstance.progInfo2Json(program_mode, prog_mode_infos);
                if (json_param == null)
                    return;

                intent.putExtra(FileExplorer.PARAM_PROGRAM_PROGINFO, json_param.toString());
                startActivity(intent);
                finish();
                */
            	
            	Intent returnIntent = new Intent();
        		setResult(RESULT_CANCELED, returnIntent);
        		FileExplorer.this.finish();
            }
        });
    }

    private void showAlert(Context context, String title, String message)
    {
       new AlertDialog.Builder(context)
         			  .setTitle(title)
        			  .setMessage(message)
        			  .setNegativeButton(R.string.common_ok, null)
        			  .show();
    }

    private void loadCurFileList()
    {
    	int HEIGHT_LAYOUT = 82;
    	int HEIGHT_IMAGE = 52;
    	int HEIGHT_TEXT = 52;
    	int MARGIN_LEFT = 8;
    	int TEXT_SIZE = 28;
    	
    	if ( m_First == 1 )
    	{
    		HEIGHT_LAYOUT *= ResolutionSet.fYpro;
    		HEIGHT_IMAGE *= ResolutionSet.fYpro;
    		HEIGHT_TEXT *= ResolutionSet.fYpro;
    		MARGIN_LEFT *= ResolutionSet.fYpro;
    		TEXT_SIZE = (int)((float)TEXT_SIZE * (float)ResolutionSet.fPro);
    	}
    	
    	m_First = 1;
    	
        if (m_nDialogMode == MODE_OPEN)
            txtTitle.setText("  打開文件: " + m_szCurPath);
        else
            txtTitle.setText("  Save File : " + m_szCurPath);

        listScrollLayout.removeAllViews();

        File curfile = new File(m_szCurPath);
        File[] childfiles = null;

        if (!curfile.exists())
        {
            showAlert(FileExplorer.this, "File Browser", "This path dosen't exist!");
            m_szCurPath = getDefaultDir();

            curfile = new File(m_szCurPath);
            if (!curfile.exists())
            {
                showAlert(FileExplorer.this, "File Browser", "Finding default directory failed!");
                finish();
                return;
            }
        }

        if (curfile.isFile())
        {
            m_szCurPath = curfile.getParent();
            curfile = new File(m_szCurPath);
        }

        childfiles = curfile.listFiles();

        /* Adding file info into scrollview */
        /* First of all, add 'up' item */
        LinearLayout fileitem_up = new LinearLayout(listScrollLayout.getContext());
        LinearLayout.LayoutParams itemLayoutParams_up = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, HEIGHT_LAYOUT);
        itemLayoutParams_up.setMargins(MARGIN_LEFT, 0, 0, 0);
        fileitem_up.setLayoutParams(itemLayoutParams_up);
        fileitem_up.setOrientation(LinearLayout.HORIZONTAL);
        fileitem_up.setGravity(Gravity.CENTER_VERTICAL);

        ImageView imgItem_up = new ImageView(fileitem_up.getContext());
        ViewGroup.LayoutParams imgLayoutParams_up = new ViewGroup.LayoutParams(HEIGHT_IMAGE, HEIGHT_IMAGE);
        imgItem_up.setLayoutParams(imgLayoutParams_up);
        imgItem_up.setBackgroundResource(R.drawable.directory_up);
        fileitem_up.addView(imgItem_up);

        TextView txtView_up = new TextView(fileitem_up.getContext());
        LinearLayout.LayoutParams txtLayoutParams_up = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, HEIGHT_TEXT);
        txtLayoutParams_up.setMargins(MARGIN_LEFT, 0, 0, 0);
        txtView_up.setLayoutParams(txtLayoutParams_up);
        txtView_up.setTextSize(TypedValue.COMPLEX_UNIT_PX, TEXT_SIZE);
        txtView_up.setText("..");
        txtView_up.setGravity(Gravity.CENTER_VERTICAL);
        txtView_up.setTextColor(Color.BLACK);

        txtView_up.setClickable(true);
        txtView_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To change body of implemented methods use File | Settings | File Templates.
                File curfile = new File(m_szCurPath);
                if (curfile == null)
                    return;

                File parentFile = curfile.getParentFile();
                if (parentFile == null)
                    return;

                if (parentFile.exists())
                {
                    m_szCurPath = curfile.getParent();
                    loadCurFileList();
                }
            }
        });

        m_arrFileList.clear();
        m_arrViewList.clear();

        fileitem_up.addView(txtView_up);
        listScrollLayout.addView(fileitem_up);

        /* Add child directory and file list */
        if (childfiles == null)
            return;

        for (int i = 0; i < childfiles.length; i++)
        {
            if (childfiles[i].isFile())
                continue;

            LinearLayout fileitem = new LinearLayout(listScrollLayout.getContext());
            LinearLayout.LayoutParams itemLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, HEIGHT_LAYOUT);
            itemLayoutParams.setMargins(MARGIN_LEFT, 0, 0, 0);
            fileitem.setLayoutParams(itemLayoutParams);
            fileitem.setOrientation(LinearLayout.HORIZONTAL);
            fileitem.setGravity(Gravity.CENTER_VERTICAL);

            ImageView imgItem = new ImageView(fileitem.getContext());
            ViewGroup.LayoutParams imgLayoutParams = new ViewGroup.LayoutParams(HEIGHT_IMAGE, HEIGHT_IMAGE);
            imgItem.setLayoutParams(imgLayoutParams);
            imgItem.setBackgroundResource(R.drawable.directory_icon);
            fileitem.addView(imgItem);

            TextView txtView = new TextView(fileitem.getContext());
            LinearLayout.LayoutParams txtLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, HEIGHT_TEXT);
            txtLayoutParams.setMargins(MARGIN_LEFT, 0, 0, 0);
            txtView.setLayoutParams(txtLayoutParams);
            txtView.setTextSize(TypedValue.COMPLEX_UNIT_PX, TEXT_SIZE);
            txtView.setText(childfiles[i].getName());

            m_arrViewList.add(txtView);
            m_arrFileList.add(childfiles[i].getPath());

            txtView.setGravity(Gravity.CENTER_VERTICAL);
            txtView.setTextColor(Color.BLACK);

            txtView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //To change body of implemented methods use File | Settings | File Templates.
                    int i = 0;
                    for (i = 0; i < m_arrViewList.size(); i++)
                    {
                        if (v == m_arrViewList.get(i))
                            break;
                    }

                    if (i >= m_arrViewList.size())
                        return;

                    File file = new File(m_arrFileList.get(i));
                    if (!file.exists())
                        return;

                    if (file.isFile())
                    {
                        if (m_nDialogMode == MODE_OPEN)
                            txtFileName.setText(file.getPath());
                        else
                            txtFileName.setText(file.getName());
                    }
                    else if (file.isDirectory())
                    {
                        m_szCurPath = file.getPath();
                        loadCurFileList();
                    }
                }
            });

            fileitem.addView(txtView);

            listScrollLayout.addView(fileitem);
        }

        for (int i = 0; i < childfiles.length; i++)
        {
            if (childfiles[i].isDirectory())
                continue;

            LinearLayout fileitem = new LinearLayout(listScrollLayout.getContext());
            LinearLayout.LayoutParams itemLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, HEIGHT_LAYOUT);
            itemLayoutParams.setMargins(MARGIN_LEFT, 0, 0, 0);
            fileitem.setLayoutParams(itemLayoutParams);
            fileitem.setOrientation(LinearLayout.HORIZONTAL);
            fileitem.setGravity(Gravity.CENTER_VERTICAL);

            ImageView imgItem = new ImageView(fileitem.getContext());
            ViewGroup.LayoutParams imgLayoutParams = new ViewGroup.LayoutParams(HEIGHT_IMAGE, HEIGHT_IMAGE);
            imgItem.setLayoutParams(imgLayoutParams);
            imgItem.setBackgroundResource(R.drawable.file_icon);
            fileitem.addView(imgItem);

            TextView txtView = new TextView(fileitem.getContext());
            LinearLayout.LayoutParams txtLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, HEIGHT_TEXT);
            txtLayoutParams.setMargins(MARGIN_LEFT, 0, 0, 0);
            txtView.setLayoutParams(txtLayoutParams);
            txtView.setTextSize(TypedValue.COMPLEX_UNIT_PX, TEXT_SIZE);
            txtView.setText(childfiles[i].getName());

            m_arrViewList.add(txtView);
            m_arrFileList.add(childfiles[i].getPath());

            txtView.setGravity(Gravity.CENTER_VERTICAL);
            txtView.setTextColor(Color.BLACK);

            txtView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //To change body of implemented methods use File | Settings | File Templates.
                    int i = 0;
                    for (i = 0; i < m_arrViewList.size(); i++)
                    {
                        if (v == m_arrViewList.get(i))
                            break;
                    }

                    if (i >= m_arrViewList.size())
                        return;

                    File file = new File(m_arrFileList.get(i));
                    if (!file.exists())
                        return;

                    if (file.isFile())
                    {
                        if (m_nDialogMode == MODE_OPEN)
                            txtFileName.setText(file.getPath());
                        else
                            txtFileName.setText(file.getName());
                    }
                    else if (file.isDirectory())
                    {
                        m_szCurPath = file.getPath();
                        loadCurFileList();
                    }
                }
            });

            fileitem.addView(txtView);

            listScrollLayout.addView(fileitem);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        setContentView(R.layout.activity_file_explore);
        
		//GlobalInstance.g_curContext = FileExplorer.this;
        
        Global.Cur_FileExplorer_SelFile = "";

        initVariables();

        loadControls();

        loadCurFileList();
        
        initActivity(R.id.rl_fileexplorer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return super.onCreateOptionsMenu(menu);    //To change body of overridden methods use File | Settings | File Templates.
    }

    private boolean saveFile(String filepath, Object obj)
    {
        boolean success = true;
        File newFile = new File(filepath);
        if (newFile == null || obj == null)
            success = false;
        else
        {
            if (newFile.exists())
                newFile.delete();

            try
            {
                FileOutputStream out = new FileOutputStream(newFile);
                out.write(obj.toString().getBytes());
                out.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                success = false;
            }
        }

        return success;
    }

    private JSONObject openFile(String filepath)
    {
        JSONObject obj = null;

        File newFile = new File(filepath);
        if (newFile != null)
        {
            try
            {
                byte[] buffer = new byte[(int)newFile.length()];
                FileInputStream inStream = new FileInputStream(newFile);
                inStream.read(buffer);
                inStream.close();

                String szBuffer = new String(buffer);
                obj = new JSONObject(szBuffer);
                /*
                if (!obj.getString("FileType").equals("LampLightInfo"))
                    obj = null;
                    */
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                obj = null;
            }
        }

        return obj;
    }
}
