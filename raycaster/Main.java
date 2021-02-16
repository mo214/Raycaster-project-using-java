/*
   Main.java
 
   Created on marts  7, 2010 (Morten Rhiger <mir@ruc.dk>)
*/

import java.util.ArrayList;
import java.awt.Color;


public class Main {
  /* Her laves trace. Den bruges i reflection l¾ngere nede i programmet. */
  private static double trace(Ray r, Scene scene, Vector3 light, Sphere shape){
    /* Something */
    Hit hit = scene.intersect(r, shape);
    /* Her laves shading*/
    double shading = 0.0;
    
    /* Hvis lyset(ray) ikke rammer noget, skal der ikke g¿res noget. Det g¿r det sort.
     * Ellers laves der skygge op objektet */      
        if (hit == null) {

          // The ray doesn't intersect with an object
       //   return 0.0; //kan ¾ndre baggrundsfarven
         
        } else {
        
          // The ray intersects with an object

          // This is where to 
          //
          // 1. use the vectors 'light' and 'hit.normal' to compute
          //    shading, and 
          
          /* Her udregnes lambertian shading med formlen z = -N¥L 
           * N = hit.normal
           * L = light
           * N ganges med -1 (multiply(-1)), hvorefter vektorern laves til en enhedsvektor.
           * Herefter udregnes prikproduktet.
           * Hvis shading er mindre end nul, s¾t den til nul, hvilket farver den sort.*/
          shading = hit.normal.multiply(-1).unitize().dot(light);
          if (shading<0) shading=0;

          // 2. use the vector 'light' and the point 'hit.point' to
          //    detect shadows.
          /* Vi laver en ny ray, der peger modsat af lyskilden (ray2). */
          Ray ray2 = new Ray(hit.point, light.multiply(-1));
          /* Der tjekkes om der lyses pŒ noget. Sk¾rer lyskilden ikke et objekt, ignoreres den 
           * Ellers laves der skygger*/
          Hit hit2 = scene.intersect(ray2, hit.shape);
          if (hit2 != null)
          shading = 0;
          
          /* Her laves specular reflection med formlen
           * R = D -2(D¥N)N
           * D = r.direction
           * N = hit.normal
           * Vi har skrevet det op sŒledes: R = D -(D * N ¥ N)*2  */
          Vector3 direction = r.direction.subtract(hit.normal.multiply(r.direction.dot(hit.normal)*2));
         
          /* Laver en ny ray og inds¾tter i reflection*/
          Ray ray3 = new Ray(hit.point, direction);
          double reflection = trace(ray3, scene, light, hit.shape);

          /* Reflektionen udregnes med formlen (geometric mean)
           * c = 1 - kvadratrod((1-a)(1-b))
           * hvor a = shading og b = reflection
           * Dette g¿res ved at kombinere shading og reflection
           */
          shading= 1.0 - Math.sqrt((1.0-shading)*(1.0-reflection)) ;
        }
    return shading;
  }
  
/* Her k¿res main*/
  public static void main(String[] args) {
    int window_width  = 400;
    int window_height = 400;


    double camera_width = 0.15;
    double camera_height = camera_width * (window_height / (double) window_width);
 
    // Setup scene
    /* Her laves scene, der er en arrayList af nye cirkler(spheres) */
    Scene scene = new Scene();
    scene.add(new Sphere(new Point3(0,   0, 0),    1));
    scene.add(new Sphere(new Point3(1,   0, 1),    0.4));
    scene.add(new Sphere(new Point3(0.4, 1, -0.6), 0.2));
 //   scene.add(new Sphere(new Point3(0.8, 0.2, -0.9), 0.2));
   
    //Light
    /* Her laves vektoren light og g¿res til en enhedsvektor*/
    Vector3 light = new Vector3(-1, -1, 0).unitize();

    // Setup display

    Display display = new Display(window_width, window_height);
    /* Her laves kameraets vinkel, der bestemmer hvordan vi ser billedet*/
    Camera camera = new Camera(new Point3(1.5, -1, -4), camera_width, camera_height); //Camera
    camera.setWindowDimensions(window_width, window_height);

    // Render
    /* Her laves billedet. 
     * x og y kommer fra klassen Camera, hvor x og y er placeringen af kameraets "¿je"
     * og som kan ses gennem "the view plane" i koordinaterne (x,y)
     * */

    for (int y = 0; y < window_height; y++) {
      for (int x = 0; x < window_width; x++) {
       Ray ray = camera.getRay(x, y);
       double intensity = trace(ray, scene, light, null);

          
    
 //Farver lyset
 display.plot(x, y, new Color((float)intensity,(float)intensity, (float)intensity));; 
// For now, draw only silhouette
        //        
      }
      display.refresh();
    }
    if (args.length == 1)
      display.save(args[0]);
  }
}
