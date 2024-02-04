(ns main)

(def scene (js/three.Scene.))

(def camera
  (js/three.PerspectiveCamera.
   75
   (/ js/window.innerWidth
      js/window.innerHeight)
   0.1
   1000))

(def renderer
  (js/three.WebGLRenderer.))

(js/document.body.appendChild renderer.domElement)

(def geom (js/three.BoxGeometry. 0.1 0.1 0.8))

(defn make-cuboids []
  (let [total-height 8.0
        per-row 9
        spacing (/ total-height per-row)]
    (for [w (range per-row)
          h (range per-row)
          d (range per-row)]
      (let [m (js/three.MeshBasicMaterial. #js
                                            {:color "#00ff00"
                                             :transparent true
                                             :opacity 0.7})
            cube (js/three.Mesh. geom m)]
        (.set cube.position
              (-
               (* w spacing)
               (/ total-height 2.0))
              (-
               (* h spacing)
               (/ total-height 2.0))
              (-
               (* d spacing)
               (/ total-height 2.0)))
        cube))))

(def cuboids (make-cuboids))

(.setSize renderer
          js/window.innerWidth
          js/window.innerHeight)

(doseq [c cuboids]
  (.add scene c))

(.setZ camera.position 2)

(defn euler [x y z]
  (js/three.Euler. x y z))

(defn v3 [x y z]
  (js/three.Vector3. x y z))

(def rotation (atom [0 0 0]))

(defn update-rotation []
  (swap! rotation
         (fn [[x y z]]
           [(+ 0.01 x)
            (+ 0.01 y)
            (+ 0.01 z)])))

(def frame (atom 0))

(defn animate []
  (swap! frame inc)
  (camera.rotation.set 0 0 (/ @frame 80.0))
  (.setZ camera.position
         (+ 3
            (* 4.0 (js/Math.sin (/ @frame 100)))))

  (update-rotation)
  (doseq [c cuboids]
    (let [new-euler (apply euler
                           (map * @rotation
                                [c.position.x
                                 c.position.y
                                 c.position.z]))]
      (.setRotationFromEuler c new-euler)
      (.set c.material.color
            (js/three.Color. (*
                              (js/Math.sin
                               (+ c.position.x
                                  c.position.y
                                  (/ @frame
                                     30))))
                             (*
                              (js/Math.cos
                               (+ c.position.y
                                  c.position.z
                                  (/ @frame
                                     100))))
                             (*
                              (js/Math.sin
                               (+ c.position.z
                                  c.position.x
                                  (/ @frame
                                     50)))))))))

(defn render []
  (js/requestAnimationFrame render)
  (animate)
  (.render renderer scene camera))

(render)
