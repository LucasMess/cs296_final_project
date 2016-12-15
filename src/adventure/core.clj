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
		:examine "A beautiful flower that seems to have recently been placed in this house for you."
	}
	:caviar{
		:name "Caviar"
		:title "some caviar"
		:on-equip "You feel like something is not right."
		:examine "It smells really bad."
		:damage -1
	}
	:candle{
		:name "Candle"
		:title "a candle"
		:use-location :bedroom-master
		:on-use "You light the candle and gently place it on the floor. There is now a romantic vibe in the room."
		:examine "It's a candle."
	}
	:sword{
		:name "Sword"
		:title "a sword"
		:on-equip "You somehow feel more powerful, even though you have never used one in your life."
		:examine "There are markings of a lion, a direwolf and a stag fighting a dragon on the handle."
		:damage 100
	}
	:knife{
		:name "Knife"
		:title "a knife"
		:on-equip "It was actually meant for butter."
		:examine "It seems to be imbued with some sort of ancient spell... oh no, it's just a butter knife."
		:damage 10
	}
	:chalk{
		:name "Chalk"
		:title "a piece of chalk"
		:use-location :bedroom-master
		:on-use "You draw some symbols on the floor with the chalk. You have never seen these markings before but a magical power guides your hand."
		:examine "This chalk seems to have been taken directly from Altgeld 314."
	}
	:old-book{
		:name "Old Book (old-book)"
		:title "an old book"
		:use-location :bedroom-master
		:on-use "You read the enchantment in the book. Nothing interesting seems to happen."
		:examine "This book is so old that it is actually made out of paper."
	}
	:cracked-pot{
		:name "Cracked Pot (cracked-pot)"
		:title "a cracked pot"
		:use-location :bathroom-lower
		:on-use "You attempt to fill the cracked pot with water, but end up getting your shoes wet."
		:on-equip "You wear it as a helmet, and it seems to fit perfectly."
		:examine "This seems a little bit broken, but maybe you can still... nope."
		:damage 2
	}
	:hourglass{
		:name "Hourglass"
		:title "an hourglass"
		:examine "Seems like a very nice time-keeping device."
	}
	:teddybear{
		:name "Teddy Bear (teddybear)"
		:title "a teddy bear"
		:use-location :bedroom
		:on-use "You place the teddy bear gently on the bed. You hear a noise outside so you look out the window. You look back and realize the teddy bear is gone."
		:on-equip "Maybe it will scare the bogeyman?"
		:examine "A mysterious power radiates from it's soulless eyes."
		:damage 9001
	}
	:rarepepe{
		:name "Rare Pepe (rarepepe)"
		:title "a rare pepe"
		:examine "One of the rarest pepes in the world. This pepe is capable of sending 90 kilobytes of memes over 300 kilometers wirelessly."
		}
	:barehands{
		:name "bare hands"
		:damage 5
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
		:desc "There are paintings covering most of the walls, and floor creaks with every step you take. With every breath you take and every move you make, you feel like someone is watching you."
		:dir {
			:south :kitchen
			:north :dining-room
			:east :hallway-upper
			:west :pantry
		} 
		:contents #{:cracked-pot}
	}


	:living-room {
		:name "The Living Room"
		:title "in the living room"
		:desc "There is an old leather couch that has been turned over. The TV is broken, but there is a static noise coming from it. You feel very disturbed by the lack of a video game console."
		:dir {
			:south :foyer
			:east :dining-room
		} 
		:contents #{:rose :caviar}
	}

	:dining-room {
		:name "The Dining Room"
		:title "in the dining room"
		:desc "The table is set, but the plates are empty. Some chairs are knocked over while others are missing, you feel as if people sitting on it left in a hurry, because there is old chocolate cake on the table, untouched."
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
		:desc "It is a very nice kitchen, but there seems to be some tomato sauce splattered on the walls. You decide to lick some of it to satiate your hunger, but somehow you feel worse. It must be old sauce."
		:dir {
			:north :hallway-lower
			:east :patio
			:west :pantry
		} 
		:contents #{:knife :hourglass}
	}

	:pantry {
		:name "The Pantry"
		:title "in the pantry"
		:desc "After scaring away the rats, you find yourself in an empty pantry. The only consumable item that seems to be left is piles and piles of garlic."
		:dir {
			:east :kitchen
		} 
		:contents #{:sword}
	}

	:patio {
		:name "The Patio"
		:title "in the patio"
		:desc "It is very cold here, but you don't mind. You hear some wolves howling in the distance."
		:dir {
			:west :kitchen
		} 
		:contents #{:teddybear}
	}

	:bathroom-lower {
		:name "The Lower Bathroom"
		:title "in the lower bathroom"
		:desc "It is a very clean bathroom."
		:dir {
			:west :dining-room
		} 
		:contents #{}
	}  

	:hallway-upper {
		:name "The Upper Hallway"
		:title "in the upper hallway"
		:desc "You hear a low rumbling noise and panic for a moment, before realizing it was your stomach. There are still a lot of paintings in this hallway, and they all seem to be looking at you."
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
		:desc "A quiet bedroom that once roomed a child. There are drawings on the wall, so you approach them to get a closer look. Some of the paintings are of a giant figure with pointy teeth. It must be one of the child's nightmares."
		:dir {
			:south :hallway-upper
			:east :bathroom-bedroom
		} 
		:contents #{:chalk}
	}     

	:study {
		:name "The Study"
		:title "in the study"
		:desc "A very peaceful room. There are bookshelves covering all the walls, and a very nice computer on one side. You get very excited and decide to boot it up. It displays the Windows Vista logo and you immediately turn it off."
		:dir {
			:north :hallway-upper
		} 
		:contents #{:old-book}
	}

	:bathroom-bedroom {
		:name "The Upper Bathroom"
		:title "in the upper bathroom"
		:desc "Seems like a very nice bathroom for a little kid. It has a bathtub, a sink and a toilet. What more can he ask for?"
		:dir {
			:west :bedroom
		} 
		:contents #{}
	}     

	:bedroom-master {
		:name "The Master Bedroom"
		:title "in the master bedroom"
		:desc "A very spacious and nice bedroom. It is oddly quiet in here, but then again, the house IS empty."
		:dir {
			:south :bathroom-master
		} 
		:contents #{}
	}

	:bathroom-master {
		:name "The Master Bathroom"
		:title "in the master bathroom"
		:desc "This bathroom is a dirtier than the rest of the house. Someone seems to have thrown a bottle of ketchup on the mirror, so you cannot see your reflection. For a moment you think you are a vampire, but then you remember that they are not real."
		:dir {
			:north :bedroom-master
		} 
		:contents #{}
	}    

	:attic {  
		:name "The Attic"
		:title "in the attic"
		:desc "You find yourself in a very tight spot. There are spiderwebs covering every inch of the room, and there is a low sobbing sound that appears to be coming from the walls. You decide to light a match, only to find yourself face to face with a pale-looking figure with red blood-shot eyes and pointy teeth: a mermaid... wait, a VAMPIRE!!!!!\n\nAfter gasping for air, you realize the vampire is asleep on top of a very special item that will end your adventure."
		:dir {
		} 
		:contents #{}
	}  
} 

)

(def adventurer
	{:location :foyer
		:inventory #{:candle :rose :chalk :old-book :teddybear}
		:tick 0
		:health 10
		:trapdoorOpen false
		:placedBlood false
		:litCandle false
		:putRosePetals false
		:drewChalkImage false
		:readBookPassage false
		:equipedItem :barehands
		:seen #{}
		:searched #{}})

(defn status [player]
	(let [location (player :location)]
		(println "")
		(print (str "You are " (-> the-map location :title) "."))
		(when-not ((player :seen) location)
			(print (str " " (-> the-map location :desc))))
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

;; Changes game booleans if the player uses a particular item.
(defn changePlayerStatus [player item]
	(case item
		:candle (assoc-in player [:litCandle] true)
		:chalk (assoc-in player [:drewChalkImage] true)
		:rose (assoc-in player [:putRosePetals] true)
		:old-book (assoc-in player [:readBookPassage] true)
		player))

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
						(update-in (changePlayerStatus player item) [:inventory] #(disj % item)))
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
				(assoc-in player [:inventory] (clojure.set/union contents (-> player :inventory)))))))

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
	(println "  equip <item> - equips an item to be used for fighting.")
	(println "  examine <item> - examines an item.")
	(println "  use <item> - uses an item.")
	(println "  read <item> - reads what is written in an item.")
	player)

(defn checkInventory [player]
	(let [items (player :inventory)]
		(println "~~~~ INVENTORY ~~~~\n")
		(if (empty? items)
			(println "*empty*")
			(doseq [item items] (println (str "* " (-> the-items item :name)))))
		(print "\n===================\n"))
		player)

(defn attack [player]
	(let [location (player :location) weapon (player :equipedItem)]
		(if (= location :attic)
			(do
				(println (str "You attack the vampire using your " (-> the-items weapon :name) "! It does " (->	the-items weapon :damage) " damage! He is dead now."))
				(println "\nYou found a Rare Pepe!")
				(assoc-in player [:inventory] (clojure.set/union (player :inventory) #{:rarepepe})))
			(do
				(println "There is nothing here to attack.")
				player))))

(defn dance [player]
	(print "You start dancing to the beat of Thriller, by Michael Jackson. ")
	(if (and 
			(= (player :location) :bedroom-master) 
			(= (player :litCandle) true)
			(= (player :drewChalkImage) true)
			(= (player :putRosePetals) true)
			(= (player :readBookPassage) true))
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

(defn printBook []
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
	(println "| |===================================="))

(defn readBook [player command]
	(if (= (second command) :old-book)
		(if ((player :inventory) :old-book)
			(printBook)
			(println "You have nothing to read!")))
	player)

(defn equipItem [player command]
	(let [item (second command)]
		(if (nil? item)
			(do
				(println "Choose an item to equip.")
				player)
			(if ((player :inventory) item)
				(if (nil? (-> the-items item :on-equip))
					(do
						(println "You cannot equip this item.")
						player)
					(do
						(println (str "You equip the " (-> the-items item :name) ". "(-> the-items item :on-equip)))
						(assoc-in player [:equipedItem] item)))
				(do
					(println "You don't have an item with that name.")
					player)))))

(defn examineItem [player command]
	(let [item (second command)]
		(if (nil? item)
			(println "Choose an item to examine.")
			(if ((player :inventory) item)
				(println (-> the-items item :examine))
				(println "You don't have an item with that name."))))
	player)

(defn debug [player]
	(println (str "location: "(player :location)))
	(println (str "candle lit: "(player :litCandle)))
	(println (str "chalk drawn: "(player :drewChalkImage)))
	(println (str "petals put: "(player :putRosePetals)))
	(println (str "read book: "(player :readBookPassage)))

	player)

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

	    ;; Equip item
	    (:equip) (equipItem player command)

	    ;; Examine item
	    (:examine) (examineItem player command)

	    ; Read book for instructions.
	    (:read) (readBook player command)

	    ;; Attack enemy.
	    (:attack) (attack player)

	    ;; Dance to open mysterious trap door.
	    (:dance) (dance player)

	    ;; Debugging
	    (:debug) (debug player)

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
