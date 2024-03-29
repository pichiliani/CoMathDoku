package net.cactii.mathdoku;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    public GridView kenKenGrid;
    TextView solvedText;
    TextView pressMenu;
    ProgressDialog mProgressDialog;
    
    LinearLayout topLayout;
    LinearLayout controls;
    Button digits[] = new Button[9];
    Button clearDigit;
    CheckBox maybeButton;
    View[] sound_effect_views;
	private Animation outAnimation;
	private Animation solvedAnimation;
	
	public SharedPreferences preferences;
    
    final Handler mHandler = new Handler();
	private WakeLock wakeLock;
	
    ////////////////////////////////////////
    // COLAB
    ///////////////////////////////////////
	private static int port = 0;
	private static String IP = "";
	private static String USER = "";
	private static String PASS = "";
	private static String SESS = "";
	private static Activity thisView;
	
	public String RECEBIDO_LOCAL = "0";
	
	
	/*** O objeto utilizado para envar os eventos   *     */
	public ClienteConecta clienteConecta = new ClienteConecta();
	
	// o Objeto handler � criado para poder mexer na thread de UI
	public Handler handler;
	/////////////////////////////////////
	
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        ////////////////////////////////////////
        // COLAB
        ///////////////////////////////////////
        MainActivity.IP =	"192.168.1.101";
        MainActivity.port =	123;
        MainActivity.USER  =	"A";
        MainActivity.PASS  =	"A";
        MainActivity.SESS =	"sc1";
        handler = new Handler();
        
        thisView = MainActivity.this;
        ///////////////////////////////////////
        
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "Mathdoku");
        
        this.topLayout = (LinearLayout)findViewById(R.id.topLayout);
        this.kenKenGrid = (GridView)findViewById(R.id.gridView);
        this.kenKenGrid.mContext = this;
        this.solvedText = (TextView)findViewById(R.id.solvedText);
        this.kenKenGrid.animText = this.solvedText;
        this.pressMenu = (TextView)findViewById(R.id.pressMenu);
        this.controls = (LinearLayout)findViewById(R.id.controls);
        digits[0] = (Button)findViewById(R.id.digitSelect1);
        digits[1] = (Button)findViewById(R.id.digitSelect2);
        digits[2] = (Button)findViewById(R.id.digitSelect3);
        digits[3] = (Button)findViewById(R.id.digitSelect4);
        digits[4] = (Button)findViewById(R.id.digitSelect5);
        digits[5] = (Button)findViewById(R.id.digitSelect6);
        digits[6] = (Button)findViewById(R.id.digitSelect7);
        digits[7] = (Button)findViewById(R.id.digitSelect8);
        digits[8] = (Button)findViewById(R.id.digitSelect9);
        this.clearDigit = (Button)findViewById(R.id.clearButton);
        this.maybeButton = (CheckBox)findViewById(R.id.maybeButton);
       
        this.sound_effect_views = new View[] { this.kenKenGrid, this.digits[0], this.digits[1],
        		this.digits[2], this.digits[3], this.digits[4], this.digits[5], this.digits[6], this.digits[7], this.digits[8],
        		this.clearDigit, this.maybeButton
        };

        solvedAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.solvedanim);
        solvedAnimation.setAnimationListener(new AnimationListener() {
            public void onAnimationEnd(Animation animation) {
              solvedText.setVisibility(View.GONE);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
          });
        
        outAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.selectorzoomout);
        outAnimation.setAnimationListener(new AnimationListener() {
            public void onAnimationEnd(Animation animation) {
              controls.setVisibility(View.GONE);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
          });
        
        this.kenKenGrid.setOnGridTouchListener(this.kenKenGrid.new OnGridTouchListener() {
			@Override
			public void gridTouched(GridCell cell) {
				if (controls.getVisibility() == View.VISIBLE) 
				{
					
					// digitSelector.setVisibility(View.GONE);
			    	if (MainActivity.this.preferences.getBoolean("hideselector", false)) 
			    	{
						controls.startAnimation(outAnimation);
						//cell.mSelected = false;
						kenKenGrid.mSelectorShown = false;
			    	} else {
						//maybeButton.setChecked((cell.mPossibles.size() > 0));
						controls.requestFocus();
			    	}
					kenKenGrid.requestFocus();
					
					
				} else {
			    	if (MainActivity.this.preferences.getBoolean("hideselector", false)) {
						controls.setVisibility(View.VISIBLE);
						Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.selectorzoomin);
						controls.startAnimation(animation);
						kenKenGrid.mSelectorShown = true;
			    	}
					//maybeButton.setChecked((cell.mPossibles.size() > 0));
					controls.requestFocus();
				}
			}
		});
        
    	this.kenKenGrid.mFace=Typeface.createFromAsset(this.getAssets(), "fonts/font.ttf");
    	this.solvedText.setTypeface(this.kenKenGrid.mFace);
        
        this.kenKenGrid.setSolvedHandler(this.kenKenGrid.new OnSolvedListener() {
    			@Override
    			public void puzzleSolved() {
    				// TODO Auto-generated method stub
    				if (kenKenGrid.mActive)
    					animText(R.string.main_ui_solved_messsage, 0xFF002F00);
    				MainActivity.this.controls.setVisibility(View.GONE);
    				MainActivity.this.pressMenu.setVisibility(View.VISIBLE);
    			}
        });
        
        for (int i = 0; i<digits.length; i++)
        	this.digits[i].setOnClickListener(new OnClickListener() {
        		public void onClick(View v) {
        			// Convert text of button (number) to Integer
        			int d = Integer.parseInt(((Button)v).getText().toString());
        			MainActivity.this.digitSelected(d);
        		}
        	});
        this.clearDigit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				MainActivity.this.digitSelected(0);
			}
        });
        
        this.maybeButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP)
					v.playSoundEffect(SoundEffectConstants.CLICK);
				return false;
			}
        	
        });
        
        newVersionCheck();
        this.kenKenGrid.setFocusable(true);
        this.kenKenGrid.setFocusableInTouchMode(true);

        
        registerForContextMenu(this.kenKenGrid);
        SaveGame saver = new SaveGame();
        if (saver.Restore(this.kenKenGrid)) {
        	this.setButtonVisibility(this.kenKenGrid.mGridSize);
        	this.kenKenGrid.mActive = true;    		
        }
    }
    
    public void onPause() {
    	if (this.kenKenGrid.mGridSize > 3) {
	    	SaveGame saver = new SaveGame();
	    	saver.Save(this.kenKenGrid);
    	}
        if (this.wakeLock.isHeld())
            this.wakeLock.release();
    	super.onPause();
    }
    
    public void onResume() {
	    if (this.preferences.getBoolean("wakelock", true))
	        this.wakeLock.acquire();
	    if (this.preferences.getBoolean("alternatetheme", true)) {
	    	//this.topLayout.setBackgroundDrawable(null);
	    	this.topLayout.setBackgroundResource(R.drawable.background);
	    	//this.topLayout.setBackgroundColor(0xFFA0A0CC);
	    	this.kenKenGrid.setTheme(GridView.THEME_NEWSPAPER);
	    } else {
	    	this.topLayout.setBackgroundResource(R.drawable.background);
	    	this.kenKenGrid.setTheme(GridView.THEME_CARVED);
	    }
	    this.kenKenGrid.mDupedigits = this.preferences.getBoolean("dupedigits", true);
	    this.kenKenGrid.mBadMaths = this.preferences.getBoolean("badmaths", true);
	    if (this.kenKenGrid.mActive && !MainActivity.this.preferences.getBoolean("hideselector", false)) {
	    	this.controls.setVisibility(View.VISIBLE);
	    }
	    this.setSoundEffectsEnabled(this.preferences.getBoolean("soundeffects", true));
	    super.onResume();
	}
    
    public void setSoundEffectsEnabled(boolean enabled) {
    	for (View v : this.sound_effect_views)
    	v.setSoundEffectsEnabled(enabled);
    }
    
    protected void onActivityResult(int requestCode, int resultCode,
    	      Intent data) {
	    if (requestCode != 7 || resultCode != Activity.RESULT_OK)
	      return;
	    Bundle extras = data.getExtras();
	    String filename = extras.getString("filename");
    	Log.d("Mathdoku", "Loading game: " + filename);
    	SaveGame saver = new SaveGame(filename);
        if (saver.Restore(this.kenKenGrid)) {
        	this.setButtonVisibility(this.kenKenGrid.mGridSize);
        	this.kenKenGrid.mActive = true;
        }
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	//Disable or enable solution option depending on whether grid is active
    	menu.findItem(R.id.checkprogress).setEnabled(kenKenGrid.mActive);

        return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        
        menu.add(0, Menu.FIRST+5 , 0, "Settings").setShortcut('3', 'c');
        
        return true;
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	if (!kenKenGrid.mActive)
    		return;
    	
		menu.add(3, 105, 0, R.string.context_menu_show_solution);
    	menu.add(2, 101, 0, R.string.context_menu_use_cage_maybes); 
    	menu.setGroupEnabled(2, false);
    	menu.add(0, 102, 0,  R.string.context_menu_reveal_cell);
    	menu.add(1, 103, 0,  R.string.context_menu_clear_cage_cells);
    	menu.setGroupEnabled(1, false);
    	menu.add(0, 104, 0,  R.string.context_menu_clear_grid);
    	menu.setHeaderTitle(R.string.application_name);

    	for (GridCell cell : this.kenKenGrid.mCages.get(this.kenKenGrid.mSelectedCell.mCageId).mCells) {
    		if (cell.isUserValueSet() || cell.mPossibles.size() > 0)
    			menu.setGroupEnabled(1, true);
    		if (cell.mPossibles.size() == 1)
    			menu.setGroupEnabled(2, true);
    	}
    }
    
    public boolean onContextItemSelected(MenuItem item) {
    	 GridCell  selectedCell = this.kenKenGrid.mSelectedCell;
    	  switch (item.getItemId()) {
    	  case 103: // Clear cage
    		  if (selectedCell == null)
    			  break;
    		  for (GridCell cell : this.kenKenGrid.mCages.get(selectedCell.mCageId).mCells) {
    			  cell.clearUserValue();
    		  }
    		  this.kenKenGrid.invalidate();
    		  break;
    	  case 101: // Use maybes
    		  if (selectedCell == null)
    			  break;
    		  for (GridCell cell : this.kenKenGrid.mCages.get(selectedCell.mCageId).mCells) {
    			  if (cell.mPossibles.size() == 1) {
    				  cell.setUserValue(cell.mPossibles.get(0));
    			  }
    		  }
    		  this.kenKenGrid.invalidate();
    		  break;
    	  case 102: // Reveal cell
    		  if (selectedCell == null)
    			  break;
    		  selectedCell.setUserValue(selectedCell.mValue);
    		  selectedCell.mCheated = true;
    		  Toast.makeText(this, R.string.main_ui_cheat_messsage, Toast.LENGTH_SHORT).show();
    		  this.kenKenGrid.invalidate();
    		  break;
    	  case 104: // Clear grid
    		  openClearDialog();
    		  break;
    	  case 105: // Show solution
    		  this.kenKenGrid.Solve();
    		  this.pressMenu.setVisibility(View.VISIBLE);
    		  break;
    	  }
		  return super.onContextItemSelected(item);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

    	int menuId = menuItem.getItemId();
    	if (menuId == R.id.size4 ||	menuId == R.id.size5 || 
    		menuId == R.id.size6 ||	menuId == R.id.size7 || 
    		menuId == R.id.size8 ||	menuId == R.id.size9) {
    		final int gridSize;
    		switch (menuId) {
    			case R.id.size4: gridSize = 4; break;  
    			case R.id.size5: gridSize = 5; break; 
    			case R.id.size6: gridSize = 6; break;
    			case R.id.size7: gridSize = 7; break;
    			case R.id.size8: gridSize = 8; break;
    			case R.id.size9: gridSize = 9; break;
    			default: gridSize = 4; break;
    		}
   	    	String hideOperators = this.preferences.getString("hideoperatorsigns", "F");
   	    	if (hideOperators.equals("T")) {
   	    		this.startNewGame(gridSize, true);
   	    		return true;
   	    	}
   	    	if (hideOperators.equals("F")) {
   	    		this.startNewGame(gridSize, false);
   	    		return true;
   	    	}
   			AlertDialog.Builder builder = new AlertDialog.Builder(this);
   			builder.setMessage(R.string.hide_operators_dialog_message)
   			       .setCancelable(false)
   			       .setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
   			           public void onClick(DialogInterface dialog, int id) {
   			                startNewGame(gridSize, true);
   			           }
   			       })
   			       .setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
   			           public void onClick(DialogInterface dialog, int id) {
   			        	   	startNewGame(gridSize, false);
   			           }
   			       });
   			AlertDialog dialog = builder.create();
   			dialog.show();
   			return true;
    	}
    	
    	switch (menuItem.getItemId()) {
   		case R.id.saveload:
            Intent i = new Intent(this, SavedGameList.class);
            startActivityForResult(i, 7);
	        return true;
   		case R.id.checkprogress:
   			int textId;
   			if (kenKenGrid.isSolutionValidSoFar())
   				textId = R.string.ProgressOK;
			else {
				textId = R.string.ProgressBad;
				kenKenGrid.markInvalidChoices();
			}
   			Toast toast = Toast.makeText(getApplicationContext(),
   									 	 textId,
   									 	 Toast.LENGTH_SHORT);
   			toast.setGravity(Gravity.CENTER,0,0);
   			toast.show();
   			return true;
   		case R.id.options:
            startActivityForResult(new Intent(
	                MainActivity.this, OptionsActivity.class), 0);  
            return true;
   		case R.id.help:
    		this.openHelpDialog();
    		return true;
    		
   		case Menu.FIRST+5:
    		this.openCollabDialog();
   			return true;

   		default:
   	        return super.onOptionsItemSelected(menuItem);
   	    }
    }
    
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
      if (event.getAction() == KeyEvent.ACTION_DOWN &&
    	  keyCode == KeyEvent.KEYCODE_BACK &&
    	  this.kenKenGrid.mSelectorShown) 
      {
      	
    	this.controls.setVisibility(View.GONE);
    	this.kenKenGrid.requestFocus();
    	this.kenKenGrid.mSelectorShown = false;
    	this.kenKenGrid.invalidate();
    	
    	
    	return true;
      }
  	  return super.onKeyDown(keyCode, event);
    }
    
    
    public void digitSelected(int value) {
    	if (this.kenKenGrid.mSelectedCell == null)
    		return;
    	
    	if (value == 0) {	// Clear Button
			this.kenKenGrid.mSelectedCell.mPossibles.clear();
    		this.kenKenGrid.mSelectedCell.setUserValue(0);
    	}
    	else {
        	if (this.maybeButton.isChecked()) {
        		if (kenKenGrid.mSelectedCell.isUserValueSet())
            		this.kenKenGrid.mSelectedCell.clearUserValue();
    			this.kenKenGrid.mSelectedCell.togglePossible(value);
        	}
        	else {
        		this.kenKenGrid.mSelectedCell.setUserValue(value);
    			this.kenKenGrid.mSelectedCell.mPossibles.clear();
        	}
    	}
    	
		////////////////////////////////////////
		// COLAB
		///////////////////////////////////////
    	// Enviando os dados
    	ArrayList l = new ArrayList();
		l.add("MainActivity");  // O nome completamente qualificiado do widget (View_controle) 
		l.add("GridCell");                  // O tipo do controle
		l.add("");                         // A opera��o 
		l.add(this.kenKenGrid.mSelectedCell.mCellNumber);  // A c�lula
		l.add(value );           // O dado                        
		l.add(this.kenKenGrid.mSelectedCell.mCageId);           // O id da cage
		    
        clienteConecta.EnviaEvento(l,"CELL_CHANGE");
        /////////////////////////////////////
    	
    	if (this.preferences.getBoolean("hideselector", false))
    		this.controls.setVisibility(View.GONE);
    	// this.kenKenGrid.mSelectedCell.mSelected = false;
    	this.kenKenGrid.requestFocus();
    	this.kenKenGrid.mSelectorShown = false;
    	this.kenKenGrid.invalidate();
    }
    
    public void digitSelectedImpl(int celula, int valor,int cage) 
    {
    	
    	
    	
    	
    	// Primeiro obter a c�lula
    	GridCell s = this.kenKenGrid.mSelectedCell;
    	
    	// Toast.makeText(this, "Celula Selecionada fora:" + String.valueOf(celula) , Toast.LENGTH_SHORT).show();
    	
    	// for (GridCell c : this.kenKenGrid.mCages.get(cage).mCells) 
    	for (GridCell c : this.kenKenGrid.mCells)
    	{
    		if (c.mCellNumber == celula)
    		{
    			s = c;
    			// Toast.makeText(this, "Celula Selecionada dentro:" + String.valueOf(celula) , Toast.LENGTH_SHORT).show();	
    		}
    		
    	}
    	
    	
    	// Agora seleciona o grid
    	this.kenKenGrid.mSelectedCell = s;
        
        this.kenKenGrid.mSelectedCell.mSelected = true;
        this.kenKenGrid.mCages.get(this.kenKenGrid.mSelectedCell.mCageId).mSelected = true;
        
        this.kenKenGrid.mTouchedListener.gridTouched(this.kenKenGrid.mSelectedCell);
        
        this.kenKenGrid.invalidate();
        
        // Agora veja qual valor colocar!
    	s.setUserValue(valor);
    	
    	// Agora faz o refresh
    	if (this.preferences.getBoolean("hideselector", false))
    		this.controls.setVisibility(View.GONE);

    	this.kenKenGrid.requestFocus();
    	this.kenKenGrid.mSelectorShown = false;
    	this.kenKenGrid.invalidate();
    	
    }
    
    
    // Create runnable for posting
    final Runnable newGameReady = new Runnable() {
        public void run() {
        	MainActivity.this.dismissDialog(0);
    	    if (MainActivity.this.preferences.getBoolean("alternatetheme", true)) {
    	    	//MainActivity.this.topLayout.setBackgroundDrawable(null);
    	    	//MainActivity.this.topLayout.setBackgroundColor(0xFFA0A0CC);
    	    	MainActivity.this.topLayout.setBackgroundResource(R.drawable.background);
    	    	MainActivity.this.kenKenGrid.setTheme(GridView.THEME_NEWSPAPER);
    	    } else {
    	    	MainActivity.this.topLayout.setBackgroundResource(R.drawable.background);
    	    	MainActivity.this.kenKenGrid.setTheme(GridView.THEME_CARVED);
    	    }
        	MainActivity.this.setButtonVisibility(kenKenGrid.mGridSize);
        	MainActivity.this.kenKenGrid.invalidate();
        }
    };
    
    public void startNewGame(final int gridSize, final boolean hideOperators) {
    	kenKenGrid.mGridSize = gridSize;
    	showDialog(0);

    	Thread t = new Thread() {
			public void run() {
				MainActivity.this.kenKenGrid.reCreate(hideOperators);
				MainActivity.this.mHandler.post(newGameReady);
			}
    	};
    	t.start();
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
	    mProgressDialog = new ProgressDialog(this);
	    mProgressDialog.setTitle(R.string.main_ui_building_puzzle_title);
	    mProgressDialog.setMessage(getResources().getString(R.string.main_ui_building_puzzle_message));
	    mProgressDialog.setIcon(android.R.drawable.ic_dialog_info);
	    mProgressDialog.setIndeterminate(false);
	    mProgressDialog.setCancelable(false);
	    return mProgressDialog;
    }
    
    public void setButtonVisibility(int gridSize) {
    	
    	for (int i=4; i<9; i++)
    		if (i<gridSize)
    			this.digits[i].setVisibility(View.VISIBLE);
    		else
    			this.digits[i].setVisibility(View.GONE);
    		
		this.solvedText.setVisibility(View.GONE);
		this.pressMenu.setVisibility(View.GONE);
    	if (!MainActivity.this.preferences.getBoolean("hideselector", false)) {
			this.controls.setVisibility(View.VISIBLE);
    	}
    }
    
    private void animText(int textIdentifier, int color) {
  	  this.solvedText.setText(textIdentifier);
  	  this.solvedText.setTextColor(color);
  	  this.solvedText.setVisibility(View.VISIBLE);
  	    final float SCALE_FROM = (float) 0;
  	    final float SCALE_TO = (float) 1.0;
  	    ScaleAnimation anim = new ScaleAnimation(SCALE_FROM, SCALE_TO, SCALE_FROM, SCALE_TO,
  	    		this.kenKenGrid.mCurrentWidth/2, this.kenKenGrid.mCurrentWidth/2);
  	    anim.setDuration(1000);
  	    //animText.setAnimation(anim);
  	  this.solvedText.startAnimation(this.solvedAnimation);
  	}
    
    private void openHelpDialog() {
        LayoutInflater li = LayoutInflater.from(this);
        View view = li.inflate(R.layout.aboutview, null); 
        TextView tv = (TextView)view.findViewById(R.id.aboutVersionCode);
        tv.setText(getVersionName() + " (revision " + getVersionNumber() + ")");
        new AlertDialog.Builder(MainActivity.this)
        .setTitle(getResources().getString(R.string.application_name) + " " + getResources().getString(R.string.menu_help))
        .setIcon(R.drawable.about)
        .setView(view)
        .setNeutralButton(R.string.menu_changes, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
              MainActivity.this.openChangesDialog();
          }
        })
        .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
          }
        })
        .show();  
    }
    
    
    private void openCollabDialog() 
    {
    	
    	// This example shows how to add a custom layout to an AlertDialog
        LayoutInflater factory = LayoutInflater.from(this);
        
        final View textEntryView = factory.inflate(R.layout.alert_dialog_text_entry, null);
        
        final EditText s = (EditText) textEntryView.findViewById(R.id.server_edit );
        s.setText(MainActivity.IP);
        
        final EditText port = (EditText) textEntryView.findViewById(R.id.port_edit );
        port.setText(  String.valueOf(MainActivity.port)  );
        
        
        final EditText u = (EditText) textEntryView.findViewById(R.id.username_edit );
        u.setText(MainActivity.USER);
        
        final EditText p = (EditText) textEntryView.findViewById(R.id.password_edit );
        p.setText(MainActivity.PASS);
        
        final EditText sess = (EditText) textEntryView.findViewById(R.id.sess_edit);
        sess.setText(MainActivity.SESS);
        
        
        Dialog meuD = new AlertDialog.Builder(MainActivity.this)
            .setIcon(R.drawable.icon)
            .setTitle("Collaboration Settings")
            .setView(textEntryView)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) 
                {

                	
                	// Toast.makeText() mostra uma mensagem de forma amig�vel!
                	// Toast.makeText(HelloMauroActivity.this, s.getText(), Toast.LENGTH_SHORT).show();

                	
                	MainActivity.IP =	s.getText().toString();
                	MainActivity.port =	Integer.valueOf(port.getText().toString());
                    
                	MainActivity.USER  =	u.getText().toString();
                	MainActivity.PASS  =	p.getText().toString();
                	MainActivity.SESS =	sess.getText().toString();

                	
                	MainActivity.this.InicializaConexao();
                    
                    Toast.makeText(MainActivity.this, "Connected!", Toast.LENGTH_SHORT).show();
                    
                	
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    // User clicked cancel so do some stuff 
                }
            })
            .create();
         
        meuD.show();
          
    }
    
    private void openChangesDialog() {
      LayoutInflater li = LayoutInflater.from(this);
      View view = li.inflate(R.layout.changeview, null); 
      new AlertDialog.Builder(MainActivity.this)
      .setTitle(R.string.changelog_title)
      .setIcon(R.drawable.about)
      .setView(view)
      .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {
            //
        }
      })
      .show();  
    }
    
    private void openClearDialog() {
        new AlertDialog.Builder(MainActivity.this)
        .setTitle(R.string.context_menu_clear_grid_confirmation_title)
        .setMessage(R.string.context_menu_clear_grid_confirmation_message)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setNegativeButton(R.string.context_menu_clear_grid_negative_button_label, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
              //
          }
        })
        .setPositiveButton(R.string.context_menu_clear_grid_positive_button_label, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				MainActivity.this.kenKenGrid.clearUserValues();
			}
        })
        .show();  
      }
    
    public void newVersionCheck() {
        int pref_version = preferences.getInt("currentversion", -1);
        Editor prefeditor = preferences.edit();
        int current_version = getVersionNumber();
        if (pref_version == -1 || pref_version != current_version) {
          //new File(SaveGame.saveFilename).delete();
          prefeditor.putInt("currentversion", current_version);
          prefeditor.commit();
          if (pref_version == -1)
        	  this.openHelpDialog();
          else
        	  this.openChangesDialog();
          return;
        }
    }
    
    public int getVersionNumber() {
        int version = -1;
          try {
              PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
              version = pi.versionCode;
          } catch (Exception e) {
              Log.e("Mathdoku", "Package name not found", e);
          }
          return version;
      }
    
    public String getVersionName() {
        String versionname = "";
          try {
              PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
              versionname = pi.versionName;
          } catch (Exception e) {
              Log.e("Mathdoku", "Package name not found", e);
          }
          return versionname;
      }
    
    private void InicializaConexao()
	{
		
       //  System.out.println("Testando o console");
        
        // FAZENDO A CONEX�O
		clienteConecta = new ClienteConecta();
		
		clienteConecta.SetaConecta(this.IP,this.port);
		
		if( clienteConecta.SetaUser(this.USER ,this.PASS))
		{
			clienteConecta.start();
		}
		
		// SETANDO A SE��O
		
		if ( this.USER.equals("A"))
		{
		
			ArrayList l = new ArrayList();
	    	l.add(this.SESS); 
	    	l.add(null); // O MODELO DA NOVA SESS�O
	    	
	    	// o terceiro elemento eh um arraylist contendo os Ids globais das Figs
	    	l.add(null);
	    	
			//  Enviando para o argo uma mensagem de 'protocolo'
			clienteConecta.EnviaEvento(l,"PROT_nova_sessao");
		}
		else
		{
			//  Enviando para o argo uma mensagem de 'protocolo'
			clienteConecta.EnviaEvento(this.SESS,"PROT_sessao_existente");
		}
	
	}
    
    public static Activity getView()
	{
		return thisView;
	}

}