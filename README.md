# beat-saber
Гречков Максим
https://vk.com/grecha03234
Алешин Александр
https://vk.com/id489740143
*Проект : Игру написать на 2х потоках — музыка и сама игра (можно выделить 3й поток для кубиков)
	Фон : статическая картинка (можно будет потом добавить изменение цвета)
	Кубики : расставляем на каждом уровне вручную (будет 2-3 уровня). Они будут точками с какой то областью (текстурки самого кубика), при попадании по которой игроком включается анимация разрубания кубика.
	Музыка : можно взять простые треки с piano-ремейком (чтобы удобнее находить биты), или какие-нибудь 8-битки.
	Появился ещё один рапозиторий - с кодом. СМОТРЕТЬ ТУДА.
Я не стал заморачиваться с библиотеками и просто подключил все. но вообще нужны только общие библиотеи для Windows и Android:
gluegen-rt и gluegen-rt-android;
jogl-all-android и jogl-all и mobile библиотеки;
jogl-test и jogl-test-android;
Вначале надо писать:
import java.awt.DisplayMode;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;