package com.pythonanywhere.jinwooking.afinal;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class PostsListActivity extends AppCompatActivity {

    LinearLayoutManager mLayoutManager; //for sorting
    SharedPreferences mSharedPref; //for saving sort settings
    RecyclerView mRecyclerView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;

    FirebaseRecyclerAdapter<Model, ViewHolder> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Model> options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_list);

        View view = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (view != null) {
                // 23 버전 이상일 때 상태바 하얀 색상에 회색 아이콘 색상을 설정
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                getWindow().setStatusBarColor(Color.parseColor("#f2f2f2"));
            }
        }else if (Build.VERSION.SDK_INT >= 21) {
            // 21 버전 이상일 때
            getWindow().setStatusBarColor(Color.BLACK);
        }

        //Actionbar
        ActionBar actionBar = getSupportActionBar();
        //set title
        actionBar.setTitle("추천 글");

        actionBar.setDisplayHomeAsUpEnabled(true);

        mSharedPref = getSharedPreferences("SortSettings", MODE_PRIVATE);
        String mSorting = mSharedPref.getString("Sort", "newest"); //where if no settingsis selected newest will be default

        if (mSorting.equals("newest")) {
            mLayoutManager = new LinearLayoutManager(this);
            //this will load the items from bottom means newest first
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
        } else if (mSorting.equals("oldest")) {
            mLayoutManager = new LinearLayoutManager(this);
            //this will load the items from bottom means oldest first
            mLayoutManager.setReverseLayout(false);
            mLayoutManager.setStackFromEnd(false);
        }

        //RecyclerView
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        //send Query to FirebaseDatabase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("Data");

        showData();
    }


    private void showDeleteDataDialog(final String currentTitle, final String currentImage) {
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(PostsListActivity.this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure to delete this post?");
        //set positive/yes button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //user pressed "Yes", delete data

                /*Whenever we publish post the parent key is automatically generated
              Since we don't know the key of the items to remove,
              we will first need to query the database to determine those keys*/
                Query mQuery = mRef.orderByChild("title").equalTo(currentTitle);
                mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ds.getRef().removeValue(); // remove values from firebase where title matches
                        }
                        //show message that post(s) deleted
                        Toast.makeText(PostsListActivity.this, "Post deleted successfully...", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //if anything goes wron get and show error message
                        Toast.makeText(PostsListActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                //delete image using reference of url from FirebaseStorage
                StorageReference mPictureRefe = getInstance().getReferenceFromUrl(currentImage);
                mPictureRefe.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //delete successfully
                        Toast.makeText(PostsListActivity.this, "Image deleted successfully...", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //unable to delete
                        //if anything goes wrong while deleting image, get and show error message
                        Toast.makeText(PostsListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
        //set negative/no button
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //user pressed "No", just dismiss dialog
                dialog.dismiss();
            }
        });
        //show dialog
        builder.create().show();
    }

    //show data
    private void showData(){
        options = new FirebaseRecyclerOptions.Builder<Model>().setQuery(mRef, Model.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Model, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Model model) {
                holder.setDetails(getApplicationContext(), model.getTitle(), model.getDescription(), model.getImage());
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //Inflating layout row.xml
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);

                ViewHolder viewHolder = new ViewHolder(itemView);
                //item click listener
                viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //get data from firebase at the position clicked
                        String mTitle = getItem(position).getTitle();
                        String mDesc = getItem(position).getDescription();
                        String mImage = getItem(position).getImage();

                        //pass this data to new activity
                        Intent intent = new Intent(view.getContext(), PostDetailActivity.class);
                        intent.putExtra("title", mTitle); // put title
                        intent.putExtra("description", mDesc); //put description
                        intent.putExtra("image", mImage); //put image url
                        startActivity(intent); //start activity
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        //get current title to delete data from firebase
                        String currentTitle = getItem(position).getTitle();
                        //get current image url to delete image from firebase storage
                        String currentImage = getItem(position).getImage();
                        //method call
                        showDeleteDataDialog(currentTitle, currentImage);
                    }
                });

                return viewHolder;
            }
        };

        //set layout as LinearLayout
        mRecyclerView.setLayoutManager(mLayoutManager);
        firebaseRecyclerAdapter.startListening();
        //set adapter to firebase recycler view
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    //handle onBackPressed(go to previous activity)
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //search data
    private void firebaseSearch(String searchText) {

        //convert string entered in SearchView to lowercase
        String query = searchText.toLowerCase();

        Query firebaseSearchQuery = mRef.orderByChild("search").startAt(query).endAt(query + "\uf8ff");


        options = new FirebaseRecyclerOptions.Builder<Model>().setQuery(firebaseSearchQuery, Model.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Model, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Model model) {
                holder.setDetails(getApplicationContext(), model.getTitle(), model.getDescription(), model.getImage());
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //Inflating layout row.xml
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);

                ViewHolder viewHolder = new ViewHolder(itemView);
                //item click listener
                viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //get data from firebase at the position clicked
                        String mTitle = getItem(position).getTitle();
                        String mDesc = getItem(position).getDescription();
                        String mImage = getItem(position).getImage();

                        //pass this data to new activity
                        Intent intent = new Intent(view.getContext(), PostDetailActivity.class);
                        intent.putExtra("title", mTitle); // put title
                        intent.putExtra("description", mDesc); //put description
                        intent.putExtra("image", mImage); //put image url
                        startActivity(intent); //start activity
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        //get current title to delete data from firebase
                        String currentTitle = getItem(position).getTitle();
                        //get current image url to delete image from firebase storage
                        String currentImage = getItem(position).getImage();
                        //method call
                        showDeleteDataDialog(currentTitle, currentImage);
                    }
                });

                return viewHolder;
            }
        };

        //set layout as LinearLayout
        mRecyclerView.setLayoutManager(mLayoutManager);
        firebaseRecyclerAdapter.startListening();
        //set adapter to firebase recycler view
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);

    }
    
    //load data into recycler view onStart
    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseRecyclerAdapter !=null){
            firebaseRecyclerAdapter.startListening();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the menu; this adds items to the action bar if it present
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Filter as you type
                firebaseSearch(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //handle other action bar item clicks here
//        if (id == R.id.action_sort) {
//            //display alert dialog to choose sorting
//            showSortDialog();
//            return true;
//        }
        if (id == R.id.action_add) {
            //start Add Post Activity
            startActivity(new Intent(PostsListActivity.this, AddPostActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    private void showSortDialog() {
//        //options to display in dialog
//        String[] sortOptions = {" Newest", " Oldest"};
//        //create alert dialog
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Sort by") //set title
//                .setIcon(R.drawable.ic_action_sort) //set icon
//                .setItems(sortOptions, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // The 'which' argument contains the index position of the selected item
//                        // 0 means "Newest" and 1 means "oldest"
//                        if (which == 0) {
//                            //sort by newest
//                            //Edit our shared preferences
//                            SharedPreferences.Editor editor = mSharedPref.edit();
//                            editor.putString("Sort", "newest"); //where 'Sort' is key & 'newest' is value
//                            editor.apply(); // apply/save the value in our shared preferences
//                            recreate(); //restart activity to take effect
//                        } else if (which == 1) {
//                            {
//                                //sort by oldest
//                                //Edit our shared preferences
//                                SharedPreferences.Editor editor = mSharedPref.edit();
//                                editor.putString("Sort", "oldest"); //where 'Sort' is key & 'oldest' is value
//                                editor.apply(); // apply/save the value in our shared preferences
//                                recreate(); //restart activity to take effect
//                            }
//                        }
//                    }
//                });
//        builder.show();
//    }

}


/*In this part i will show how to delete specific data from the RecyclerView/Firebase
 * We will apply it on OnItemLongClick Listener
 * OnLong clicking item will show alert dialog confirming to delete or not*/