package com.example.jfransen44.tdl;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private static final int ENTER_TASK_REQUEST = 1;
    private String taskString = "";
    private String taskDate = "";
    private String taskTime = "";
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private Button clearButton;
    private Button addButton;
    private Firebase fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get widgets from xml
        listView = (ListView) findViewById(R.id.listView);
        //final EditText text = (EditText) findViewById(R.id.todoText);
        addButton = (Button) findViewById(R.id.addButton);
        clearButton = (Button) findViewById(R.id.clearButton);

        // Create a new Adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, android.R.id.text1);

        // Assign adapter to ListView
        listView.setAdapter(adapter);
        listView.setChoiceMode(listView.CHOICE_MODE_MULTIPLE);

        // Use Firebase to populate the list.
        Firebase.setAndroidContext(this);

        fb = new Firebase("https://glowing-torch-1077.firebaseio.com/todoItems");
        fb.addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                adapter.add((String) dataSnapshot.child("text").getValue());
            }

            public void onChildRemoved(DataSnapshot dataSnapshot) {
                adapter.remove((String) dataSnapshot.child("text").getValue());
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        //set up listeners
        setUpClearButtonListener();
        setupLongClickListener();
        setUpAddButton();
        setBackgroundColorListener();

    }//end onCreate

    //called when user clicks add task button; start new activity
    public void getTask(View view){
        Intent getTaskIntent = new Intent(this, AddTaskActivity.class);
        startActivityForResult(getTaskIntent, ENTER_TASK_REQUEST);
    }//end getTask

    //get results from AddTaskActivity
    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ENTER_TASK_REQUEST) {
            if (resultCode == RESULT_OK) {
                this.taskString = data.getStringExtra("taskString");
                this.taskDate = data.getStringExtra("dateString");
                this.taskTime = data.getStringExtra("timeString");
                if(! this.taskString.isEmpty()) {
                    fb.push().child("text").setValue(taskString + " " + taskDate + " " + " " + taskTime);
                }
                else
                    emptyTaskDialog();
            }

        }
    }//end onActivityResult

    //set firebase delete items
    private void deleteItem(int position){
        fb.orderByChild("text").equalTo((String) listView.getItemAtPosition(position))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            DataSnapshot firstChild = dataSnapshot.getChildren()
                                    .iterator().next();
                            firstChild.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });
    }

    //set add button listener
    private void setUpAddButton() {
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getTask(v);
            }
        });
    }

    public void setBackgroundColorListener(){
        //change item background color if checkbox is selected
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listView.isItemChecked(position))
                    view.setBackgroundColor(Color.GRAY);
                else
                    view.setBackgroundColor(Color.TRANSPARENT);
            }
        });
    }

    //set long click listener
    public void setupLongClickListener(){
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View item, int position, long id) {
                makeDialogBox(position, item);
                item.setBackgroundColor(Color.CYAN);
                return true;
            }
        });
    }//end setupLongClickListener

    //delete selected items
    private void setUpClearButtonListener(){
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (listView.isItemChecked(i)) {
                        deleteItem(i);
                        listView.setItemChecked(i, false);
                    }
                }
            }
        });
    }//end setUpClearButtonListener

    //Create dialog boxes

    //Create dialog box with buttons for delete task, send task to email, and cancel
    //accepts int position as reference to the location in adapter of selected item
    private void makeDialogBox(final int position, View item) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View newItem = item;
        builder.setMessage(R.string.builderMessage);
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.negButtonMessage, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                newItem.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        //set up button and listener for delete
        builder.setPositiveButton(R.string.posButtonMessage, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem(position);
            }
        });


        //set up button and listener for send task to email
        builder.setNeutralButton(R.string.create_mail, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent createEmail = new Intent(Intent.ACTION_SEND);
                createEmail.setType("plain/text");
                createEmail.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
                createEmail.putExtra(Intent.EXTRA_TEXT, adapter.getItem(position).toString());
                startActivity(Intent.createChooser(createEmail, "Send mail"));
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        Button posButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button negButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        posButton.setBackgroundColor(Color.RED);
        posButton.setTextColor(Color.BLACK);
        negButton.setTextColor(Color.BLACK);
    }//end makeDialogBox

    //create a dialog box if task is empty
    private void emptyTaskDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Description is empty.");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }//end emptyTaskDialog
}

