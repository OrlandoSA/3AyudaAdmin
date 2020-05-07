package ayuda.a3ayuda

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.activity_perfil.*
import kotlinx.android.synthetic.main.comentario.view.*

class PerfilActivity : AppCompatActivity() {
    var listaComentarios=ArrayList<Comentario>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)
        cargarComentarios()
        var adaptador=AdaptadorComentarios(this,listaComentarios)
        listview.adapter=adaptador
    }

    fun cargarComentarios(){
        listaComentarios.add(Comentario("Hitomi","No trabajo nada"))
        listaComentarios.add(Comentario("Hitomi","solo me saco dinero"))
        listaComentarios.add(Comentario("Hitomi","me destruyo e jardin"))
        listaComentarios.add(Comentario("Hitomi","pinto a mi perro"))
        listaComentarios.add(Comentario("Hitomi","orino mis arboles"))
    }
    private class AdaptadorComentarios: BaseAdapter {
        var comentario=ArrayList<Comentario>()
        var contexto: Context?=null

        constructor(contexto: Context, comentario:ArrayList<Comentario>){
            this.contexto=contexto
            this.comentario=comentario
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var coment=comentario[position]
            var inflador= LayoutInflater.from(contexto)
            var vista=inflador.inflate(R.layout.comentario,null)

            vista.comentario_nombre.setText(coment.nombre)
            vista.comentario_contenido.setText(coment.comentario)


            return vista
        }

        override fun getItem(position: Int): Any {
            return comentario[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return comentario.size
        }
    }
}
