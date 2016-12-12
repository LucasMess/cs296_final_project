(ns adventure.core
	(:require [clojure.core.match :refer [match]]
		[clojure.string :as str])
	(:gen-class))

(def the-items {
	:rose{
		:name "Rose"
		:title "a rose"
		:desc "A delicate red flower with painful thorns."
		:use-location :bedroom-master
		:on-use "You strip the petals off the rose delicately and place them on the wooden floor."
	}
	:caviar{
		:name "Caviar"
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
			:east :bedroom-master
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
		:health 10
		:trapdoorOpen false
		:placedBlood true
		:litCandle true
		:putRosePetals true
		:drewChalkImage true
		:readBookPassage true
		:seen #{}
		:searched #{}})

(defn status [player]
	(let [location (player :location)]
		(println "")
		(print (str "You are " (-> the-map location :title) "."))
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
			(do
				(println "Choose an item to use.")
				player)
			(if ((player :inventory) item)
				(if (= (-> the-items item :use-location) location)
					(do
						(println (-> the-items item :on-use))
						(update-in player [:inventory] #(disj % item)))
					(do
						(println "Nothing interesting happens.")
						player))
				(do
					(println "You don't have an item with that name.")
					player)))))

(defn getItemsInRoom [player]
	(let [location (player :location) contents (-> the-map location :contents)]
		(if (empty? contents)
			(do 
				(println "You find nothing interesting in this room.")
				player)
			(do
				(doseq [item contents] (println (str "You found " (-> the-items item :title) ".")))
				(assoc-in player [:inventory] (conj contents (player :inventory)))))))

(defn searchRoom [player]
	(let [location (player :location)]
		(if ((player :searched) location)
			(do 
				(println "You have already searched this room.")
				player)
			(getItemsInRoom (update-in player [:searched] #(conj % location))))))
		

(defn showHelp [player]
	(println "Here is a useful list of commands that you can use in this game:")
	(println "  north, n, up - goes to the room to the north.")
	(println "  south, s, down - goes to the room to the south.")
	(println "  east, e, right - goes to the room to the east.")
	(println "  west, w, left - goes to the room to the west.")
	(println "  look - takes a moment to enjoy the architecture and decoration.")
	(println "  help - shows this.")
	(println "  search, s - searches the room for interesting items.")
	(println "  attack - attacks the nearest enemy.")
	(println "  dance - to reduce the tension.")
	(println "  inventory, i - check items in the inventory.")
	(println "  climb - climb up or down.")
	player)

(defn checkInventory [player]
	(let [items (player :inventory)]
		(println "~~~~ INVENTORY ~~~~\n")
		(if (empty? items)
			(println "*empty*")
			(doseq [item items] (println (str "* " (-> the-items item :name)))))
		(print "\n==================="))
		player)

(defn attack [player]
	(let [location (player :location)]
		(if (= location :attic)
			(println "You attack the vampire! He is dead now.")
			(println "There is nothing here to attack.")))
	player)

(defn dance [player]
	(print "You start dancing to the beat of Thriller, by Michael Jackson. ")
	(if (and 
			(= (player :location) :bedroom-master) 
			(= (player :placedBlood) true)
			(= (player :litCandle) true)
			(= (player :drewChalkImage) true)
			(= (player :putRosePetals) true))
		(do
			(print "You dance so well that the blood on the floor lights up, the candle is blown out, and a mysterious trap door becomes visible on the ceiling.\n")
			(assoc-in player [:trapdoorOpen] true))
		(do
			(print "Nothing intersting happens, but you feel better about your dancing abilities.\n")
			player)))

(defn climbUpTrapdoor [player]
	(if (= (player :location) :bedroom-master)
		(if (= (player :trapdoorOpen) true)
			(assoc-in player [:location] :attic)
			(do
				(println "There is nothing here to climb... yet.")
				player))
		(if (= (player :location) :attic)
			(assoc-in player [:location] :bedroom-master)
			(do
				(println "There is nothing here to climb.")
				player))))

(defn printBook [player]
	(println "You start reading the book. Some words have faded away with time.")
	(println "")
	(println "///====================================")
	(println "| |------------------------------------")
	(println "| |  ~~ Ritual for Opening Portal ~~  ")
	(println "| |")
	(println "| | Many people have looked for the se-")
	(println "| | cret &#*() that !*(@) to the cur-")
	(println "| | sed lair of the !(*)) vile vampire ")
	(println "| | in the world. #&$* have all failed.")
	(println "| |")
	(println "| | But I know the way:")
	(println "| |")
	(println "| | 1. Light a candle and place it in")
	(println "| |    the main bedroom in this house.")
	(println "| | 2. Draw a ritual symbol @&*^#*!()@")
	(println "| | 3. @^&#&(* rose @&*#^&* floor.")
	(println "| | 4. @^&*@& incantation in this book. ")
	(println "| | 5. ^&*()*(*)!*@)(!(")
	(println "| |------------------------------------")
	(println "| |====================================")
	player)

(defn readBook [player]
	(if ((player :inventory) :old-book)
		(printBook player)
		(do
			(println "You have nothing to read!")
			player)))

(defn respond [player command]
	(println "")
	(case (first command)
	    ; Quit the game.
	    (:quit :q) (System/exit 0)

	    ; Show help text
	    (:help) (showHelp player)

	    ; Shows the description of the room again.
	    (:look) (update-in player [:seen] #(disj % (-> player :location)))

	    ; Use item.
	    (:use) (useItem player command)

	    ; Read book for instructions.
	    (:read) (readBook player)

	    ;; Attack enemy.
	    (:attack) (attack player)

	    ;; Dance to open mysterious trap door.
	    (:dance) (dance player)

	    ;; Climb up the mysterious trap door.
	    (:climb) (climbUpTrapdoor player)

	    ;; Check inventory
	    (:inventory :i) (checkInventory player) 

	    ; Directions
	    (:s :south :down) (go :south player)
	    (:n :north :up) (go :north player)
	    (:e :east :right) (go :east player)
	    (:w :west :left) (go :west player)

	    ; Search the room for items.
	    (:search) (searchRoom player)

	    (do (println "I don't understand you.")
	    	player)))

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
