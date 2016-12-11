(ns adventure.core
  (:require [clojure.core.match :refer [match]]
            [clojure.string :as str])
  (:gen-class))

(def the-items {
  :rose{
      :name "Rose"
      :title "a rose"
      :desc "A delicate red flower with painful thorns."
      :use-location :bathroom-master
      :on-use "You strip the petals off the rose delicately and place them on the wooden floor."
    }
  :caviar{
      :title "some caviar"
  }
  })

(def the-map
  {
    :foyer {
        :name "The Foyer"
        :title "in the foyer"
        :desc "The room is dimly lit by the occasional lightning, but you can clearly see the peeling wallpaper and the dense spider webs that populate the walls. Behind the wallpaper you see a yellow glue that once held it to the wall. There are numerous bugs stuck to this glue, so you decide to stop looking at it. There is a dark rug covering most the floor. Footsteps are barely visible on it, but the surrounding wooden floor has a thick layer of dust. You get the feeling that no one has been in here in years."
        :dir {
          :south :pantry
          :north :living-room
          :east :hallway-lower
        } 
        :contents #{}
    }
    
    :hallway-lower {
        :name "The Entrance Hallway"
        :title "in the entrance hallway"
        :desc "The room is dimly lit by the occasional lightning, but you can clearly see the peeling wallpaper and the dense spider webs that populate the walls. Behind the wallpaper you see a yellow glue that once held it to the wall. There are numerous bugs stuck to this glue, so you decide to stop looking at it. There is a dark rug covering most the floor. Footsteps are barely visible on it, but the surrounding wooden floor has a thick layer of dust. You get the feeling that no one has been in here in years."
        :dir {
          :south :kitchen
          :north :dining-room
          :east :hallway-upper
          :west :pantry
        } 
        :contents #{}
    }


    :living-room {
        :name "The Living Room"
        :title "in the living room"
        :desc ""
        :dir {
          :south :foyer
          :east :dining-room
        } 
        :contents #{:rose :caviar}
    }

    :dining-room {
        :name "The Dining Room"
        :title "in the dining room"
        :desc ""
        :dir {
          :south :hallway-lower
          :east :bathroom-lower
          :west :living-room
        } 
        :contents #{:candle}
    }    

    :kitchen {
        :name "The Kitchen"
        :title "in the kitchen"
        :desc ""
        :dir {
          :north :hallway-lower
          :east :patio
          :west :pantry
        } 
        :contents #{:knife}
    }

    :pantry {
        :name "The Pantry"
        :title "in the pantry"
        :desc ""
        :dir {
          :east :kitchen
        } 
        :contents #{:sword}
    }

    :patio {
        :name "The Patio"
        :title "in the patio"
        :desc ""
        :dir {
          :west :kitchen
        } 
        :contents #{}
    }

    :bathroom-lower {
        :name "The Lower Bathroom"
        :title "in the lower bathroom"
        :desc ""
        :dir {
          :west :dining-room
        } 
        :contents #{}
    }  

    :hallway-upper {
        :name "The Upper Hallway"
        :title "in the upper hallway"
        :desc ""
        :dir {
          :west :hallway-lower
          :north :bedroom
          :south :study
          :east :bathroom-master
        } 
        :contents #{}
    }

    :bedroom {
        :name "The Bedroom"
        :title "in the bedroom"
        :desc ""
        :dir {
          :south :hallway-upper
          :east :bathroom-bedroom
        } 
        :contents #{:chalk}
    }     

    :study {
        :name "The Study"
        :title "in the study"
        :desc ""
        :dir {
          :north :hallway-upper
        } 
        :contents #{:old-book}
    }

    :bathroom-bedroom {
        :name "The Upper Bathroom"
        :title "in the upper bathroom"
        :desc ""
        :dir {
          :west :bedroom
        } 
        :contents #{}
    }     

    :bedroom-master {
        :name "The Master Bedroom"
        :title "in the master bedroom"
        :desc ""
        :dir {
          :south :bathroom-master
        } 
        :contents #{}
    }

    :bathroom-master {
        :name "The Master Bathroom"
        :title "in the master bathroom"
        :desc ""
        :dir {
          :north :bedroom-master
        } 
        :contents #{}
    }    

    :attic {  
        :name "The Attic"
        :title "in the attic"
        :desc ""
        :dir {
        } 
        :contents #{}
    }  
  } 

 )

(def adventurer
  {:location :foyer
   :inventory #{}
   :tick 0
   :seen #{}
   :searched #{}})

(defn status [player]
  (let [location (player :location)]
    (println)
    (println (str "~~~~ " (-> the-map location :name) " ~~~~"))
    (println)
    (print (str "You are " (-> the-map location :title) ". "))
    (when-not ((player :seen) location)
      (print (-> the-map location :desc)))
    (update-in player [:seen] #(conj % location))))

(defn to-keywords [commands]
  (mapv keyword (str/split commands #"[.,?! ]+")))

(defn go [dir player]
  (let [location (player :location)
        dest (->> the-map location :dir dir)]
    (if (nil? dest)
      (do (println "You can't go that way.")
          player)
      (assoc-in player [:location] dest))))

(defn tock [player]
  (update-in player [:tick] inc))

(defn useItem [player command]
  (let [item (second command) location (player :location)]
    (if (nil? item)
      (println "Choose an item to use.")
      (if ((-> the-items item :use-location) location)
        (println "Using item.")
        (println "Nothing interesting happens.")))))

(defn getItemsInRoom [player]
  (let [location (player :location) contents (-> the-map location :contents)]
    (if (empty? contents)
      (println "You find nothing interesting in this room.")
      (doseq [item contents] (println (str "You found " (-> the-items item :title) "."))))))

(defn searchRoom [player]
  (let [location (player :location)]
    (when-not ((player :searched) location)
      (getItemsInRoom player))
    (when ((player :searched) location)
      (println "You have already searched this room."))
    (update-in player [:searched] #(conj % location))))

(defn showHelp [player]
  (println "Here is a useful list of commands that you can use in this game:")
  (println "  north, n, up")
  (println "  south, s, down")
  (println "  east, e, right")
  (println "  west, w, left")
  (println "  look")
  (println "  help")
  (println "  search, s")
  player)

(defn respond [player command]
  (println "")
  (case (first command)

    ; Quit the game.
    (:quit :q) (System/exit 0)

    ; Show help text
    (:help) (showHelp player)

    ; ???
    (:look) (update-in player [:seen] #(disj % (-> player :location)))

    ; Use item.
    (:use) (useItem player command)

    ; Directions
    (:s :south :down) (go :south player)
    (:n :north :up) (go :north player)
    (:e :east :right) (go :east player)
    (:w :west :left) (go :west player)

    ; Search the room for items.
    (:search) (searchRoom player)

    (do (println "I don't understand you.")
          player)

    ))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println)
  (println "~~~~ Haunted House ~~~~")
  (println)
  (println "You were walking alone in the woods on a gloomy Monday afternoon. The sky turned dark and a slight drizzle quickly became a thunderstorm. You see a house in the distance, and decide to seek shelter inside. The outside consisted of rotten wood and broken windows. The doorknob was colder than ice, and touching it sent chills up your spine. You sigh before adventuring inside. You feel like you're going to have a bad time.")
  (loop [local-map the-map
         local-player adventurer]
    (let [pl (status local-player)
          _  (println " What do you want to do?\n")
          command (read-line)]
      (recur local-map (respond pl (to-keywords command))))))
