package ayuda.a3ayuda

import android.app.Person
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.android.synthetic.main.activity_creacion_perfil.*
import kotlinx.android.synthetic.main.activity_registro_google.*


class RegistroGoogle : AppCompatActivity() {

    lateinit var googleSignInClient : GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private val TAG = "RegistroGoogle"
    private val RC_SIGN_IN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_google)
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .requestScopes(Scope(Scopes.PLUS_ME))
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        auth = FirebaseAuth.getInstance()
        sign_in_button.setOnClickListener{
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if(account!=null){
            firebaseAuthWithGoogle(account)
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct?.id!!)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    obtenerPerfil()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    fun obtenerPerfil(){
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            val currentUserId=acct.email
            if(currentUserId!=null){
            var mFirestore = FirebaseFirestore.getInstance()
            mFirestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
            mFirestore
                .collection("perfiles").document(currentUserId)
                .get()
                .addOnSuccessListener { document ->
                    try {
                        var perfil: Perfil? = null
                        if (document != null) {
                            var perfilAux = document.toObject(Perfil::class.java) ?: Perfil()
                            if (perfilAux.correo.equals(auth.currentUser?.email.toString())) {
                                perfil = perfilAux
                            }
                        }
                        updateUI(perfil)
                    } catch (ex: Exception) {
                        Log.e("tag", ex.message)
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Error al recibir datos!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun updateUI(perfil:Perfil?) {
        if(perfil!=null){
            val intent = Intent(this, PerfilActivity::class.java)
            intent.putExtra("nombre",perfil.nombre)
            intent.putExtra("servicio",perfil.servicio)
            intent.putExtra("correo",perfil.correo)
            intent.putExtra("edad",perfil.edad)
            startActivity(intent)
        }else{
            val intent = Intent(this, CreacionPerfil::class.java)
            startActivity(intent)
        }
    }
}
