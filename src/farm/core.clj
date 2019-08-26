(ns farm.core
  (:require [korma.db :as km]
            [clojure.java.jdbc :as j]))

(def db (km/h2 {:db "./resources/db/farm.db"}))

(km/defdb korma-db db)


(defn acre-table-name
  [domain]
  (str domain "_acre"))


(defn worker-table-name
  [domain]
  (str domain "_worker"))


(defn banana-table-name
  [domain]
  (str domain "_banana"))


(defn banana-bunch-table-name
  [domain]
  (str domain "_banana_bunch"))


(defn acre-table-ddl
  [domain]
  (j/create-table-ddl (acre-table-name domain)
                       [[:acre_id :identity "not null" "auto_increment"]
                        [:tags :varchar]
                        [:worker_id :bigint "not null"]
                        [:supervisor_id :bigint]
                        ]))


(defn worker-table-ddl
  [domain]
  (j/create-table-ddl (worker-table-name domain)
                      [[:worker_id :identity "not null" "auto_increment"]
                       [:tags :varchar]
                       [:name :varchar "not null"]
                       [:lc1_letter :image]
                       [:national_id :image]
                       [:photo :image]
                       [:supervisor_id :bigint]
                       [:comments :text]
                       ]))

(defn banana-table-ddl
  [domain]
  (j/create-table-ddl (banana-table-name domain)
                      [[:plant_id :identity "not null" "auto_increment"]
                       [:acre_id :bigint "not null"]
                       [:tags :varchar]
                       [:type :varchar]
                       [:variety :varchar]
                       [:comments :text]
                       ]))


(defn banana-bunch-table-ddl
  [domain]
  (j/create-table-ddl (banana-bunch-table-name domain)
                      [[:bunch_id :identity "not null" "auto_increment"]
                       [:plant_id :bigint "not null"]
                       [:tags :varchar]
                       [:flowered :timestamp]
                       [:ready_for_harvest :timestamp]
                       [:harvested :timestamp]
                       [:sale_price :int]
                       [:weight :int]
                       [:hands :int]
                       [:fingers :int]
                       [:comments :text]
                       [:photo :image]
                       [:unaccounted :boolean]
                       ]))


(defn create-index-ddl
  [table-name column-name]
  (str "CREATE INDEX " column-name " ON " table-name "(" column-name ");"))


(create-index-ddl "burhani_farms_banana_bunch" "flowered")




(defn create-tables
  [domain]
  (j/db-do-commands db
                    [(acre-table-ddl domain)
                     (worker-table-ddl domain)
                     (banana-table-ddl domain)
                     (banana-bunch-table-ddl domain)]))


(create-tables "saifee")



(comment
  (j/execute! db (acre-table-ddl "saifee"))
  (j/execute! db worker-table)
  (j/execute! db (banana-table-ddl "saifee"))
  (j/execute! db banana-bunch-table)

  (j/execute! db "DROP TABLE saifee_banana")

  (j/execute! db (create-index-ddl "burhani_farms_banana_bunch" "flowered"))



  (j/execute! db "SHOW INDEX FROM burhani_farms_banana_bunch ;")

  (j/insert-multi! db
                   (worker-table-name "saifee")
                   (map (fn [v] {:name v}) ["Allan" "Joshua" "Dominic" "Monday"]))

  (j/insert-multi! db
                   (acre-table-name "saifee")
                   (map (fn [v] {:worker_id v}) [1 1 2 2 3 3 4 4]))

  (j/insert-multi! db
                   (banana-table-name "saifee")
                   (for [acre (range 1 9) banana (range 1 431)]
                     {:acre_id acre}))

 )


(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

;; divide the farm into acres
;; each plant has a number in an acre
;; acres could be tagged for further classification - supervisor, produce, soil type etc

;; key is acre
;; key is plant

;; banana status

(def flowered ::flowered)

(def ready-for-harvest ::ready-for-harvest)

(def harvested ::harvested)

;; document for each acre
(def acre-1 {:acre-id 1
             :tags [::banana]
             :supervisor ["alan"]
             :worker ["bunny"]})


;; the current can contain multiple stems
;; the status of each stem can be changed
;; only when it is harvested then it is promoted to historical.
;; a new stem is added once it is harvested and if it is empty.
;; 


;; document for each plant
(def plant-1 {:plant-id 1,
              :acre-id 1,
              :tags [::matoke],
              :current [{:id "uuid"
                         :flowered "timestamp 1"
                         :ready-for-harvest "timestamp 2"}
                        ],
              :historical [{:id "uuid 2"
                            :flowered "timestamp 1"
                            :ready-for-harvest "timestamp 2"
                            :unaccounted true
                            :comment "It was stolen"}
                           {:timestamp "b" :status ::harvested :weight 20 :price 10000}]})

;;; unaccounted
;; bananas that should have been harvested - flowered more than 4 months - red
;; bananas that should have flowered - more than 2 months since last harvest

;;; dashboard
;; bananas that are ready to harvest. (overdue in orange)
;; bananas that should be ready to harvest (flowered more than 3 months  - orange, flowered more than 4 months - red)
;; bananas that should have flowered - more than 2 months since last harvest.
;; total bananas unaccounted for
;; average price, average weight, average bunches per plant, average bunches per acre, highest bunches per acre, lowest bunches per acre, highest bunches per plant last year, lowest bunches per plant year.

;;; history about a banana
;; tabular and graphical view, with graphical being constant
;; display in a table the history
;; on the graph chart the different
;; bunches till date, bunches in last 12 months, average bunch weight, average bunch price

;;; acre view
;; bananas that are ready to harvest. (overdue in orange)
;; bananas that should be ready to harvest (flowered more than 3 months  - orange, flowered more than 4 months - red)
;; bananas that should have flowered - more than 2 months since last harvest.
;; average bunches in the acre, average weight of bananas in this acre, total bunches in last 12 months.

;;; supervisor view

;;; worker view
