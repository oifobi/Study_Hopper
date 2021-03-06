package study_dev.testbed.studyhopper.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import study_dev.testbed.studyhopper.R;
import study_dev.testbed.studyhopper.models.Group;

public class MyGroups extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private CollectionReference groupRef = db.collection("users")
            .document(getUserName()).collection("groups");

    private GroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);

        // Enable back button
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Establishes the recycler view for the dropdown menu for groups
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        Query query = groupRef.orderBy("groupName", Query.Direction.DESCENDING);

        query.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

            }
        });

        FirestoreRecyclerOptions<Group> options = new FirestoreRecyclerOptions.Builder<Group>()
                .setQuery(query, Group.class)
                .build();

        adapter = new GroupAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.group_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_groups_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.create_group_option:
                Intent intent = new Intent(MyGroups.this, CreateGroup.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;

            case R.id.search_group_option:
                // TODO implement search for group activity
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(getBaseContext(), Dashboard.class);
        startActivity(in);
        overridePendingTransition(0, 0);
    }

    private String getUserName() {
        String email = mAuth.getCurrentUser().getEmail();
        int parseIndex = email.indexOf('@');
        return email.substring(0, parseIndex);
    }

}
