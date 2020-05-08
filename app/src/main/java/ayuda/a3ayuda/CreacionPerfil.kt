package ayuda.a3ayuda

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_creacion_perfil.*


class CreacionPerfil : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creacion_perfil)

        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            et_nombre.setText(acct.givenName+" "+acct.familyName);
            et_correo.setText(acct.email)
            val personPhoto: Uri? = acct.photoUrl
            Picasso.get().load(personPhoto).into(iv_foto)
        }
        bt_guardar.setOnClickListener {
            guardarPerfil()
        }
    }

    private fun guardarPerfil(){
        if(!et_nombre.text.isBlank()&&!et_correo.text.isBlank()&&!et_edad.text.isBlank()&&!et_servicios.text.isBlank()){
            val acct = GoogleSignIn.getLastSignedInAccount(this)
            if (acct != null) {
                val currentUserId = acct.email
                if (currentUserId != null) {
                    var perf = Perfil(
                        et_nombre.text.toString(),
                        et_servicios.text.toString(),
                        et_correo.text.toString(),
                        Integer.parseInt(et_edad.text.toString())
                    )
                    var mFirestore = FirebaseFirestore.getInstance()
                    mFirestore.collection("perfiles").document(currentUserId).set(perf)
                    Toast.makeText(this, "Perfil creado!", Toast.LENGTH_SHORT)
                    updateUI(perf)
                }
            }
        }else{
            Toast.makeText(this,"Debe introducir todos los datos",Toast.LENGTH_SHORT)
        }
    }

    private fun updateUI(perfil:Perfil) {
        val intent = Intent(this, PerfilActivity::class.java)
        intent.putExtra("nombre",perfil.nombre)
        intent.putExtra("servicio",perfil.servicio)
        intent.putExtra("correo",perfil.correo)
        intent.putExtra("edad",perfil.edad)
        startActivity(intent)

    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}
