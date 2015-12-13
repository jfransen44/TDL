package com.example.jfransen44.tdl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get widgets from xml
        final ListView listView = (ListView) findViewById(R.id.listView);
        final EditText text = (EditText) findViewById(R.id.todoText);
        final Button button = (Button) findViewById(R.id.addButton);

        // Create a new Adapter
        final ArrayAdapter adapter = new ArrayAdapter<>(this,
           android.R.layout.simple_list_item_multiple_choice, android.R.id.text1);

        // Assign adapter to ListView
        listView.setAdapter(adapter);
        listView.setChoiceMode(listView.CHOICE_MODE_MULTIPLE);

        // Use Firebase to populate the list.
        Firebase.setAndroidContext(this);

        new Firebase("https://YOUR-FIREBASE-APP.firebaseio.com/todoItems")
           .addChildEventListener(new ChildEventListener() {
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

        // Add items via the Button and EditText at the bottom of the window.

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getTask(v);
            }
        });

        // Delete items when clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                new Firebase("https://YOUR-FIREBASE-APP.firebaseio.com/todoItems")
                        .orderByChild("text")
                        .equalTo((String) listView.getItemAtPosition(position))
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChildren()) {
                                    DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                                    firstChild.getRef().removeValue();
                                }
                            }

                            public void onCancelled(FirebaseError firebaseError) {
                            }
                        });
            }
        });
    }

    //called when user clicks add task button
    public void getTask(View view){
        Intent getTaskIntent = new Intent(this, AddTaskActivity.class);
        startActivityForResult(getTaskIntent, ENTER_TASK_REQUEST);
    }

    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ENTER_TASK_REQUEST) {
            if (resultCode == RESULT_OK) {
                this.taskString = data.getStringExtra("taskString");
                this.taskDate = data.getStringExtra("dateString");
                this.taskTime = data.getStringExtra("timeString");
                new Firebase("https://YOUR-FIREBASE-APP.firebaseio.com/todoItems")
                        .push()
                        .child("text")
                        .setValue(taskString + " " + taskDate + " " + " " + taskTime);
            }

        }
    }
}
