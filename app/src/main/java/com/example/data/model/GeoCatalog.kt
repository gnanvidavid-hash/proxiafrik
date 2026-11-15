package com.example.data.model

object GeoCatalog {
    data class DepartmentInfo(
        val name: String,
        val cities: List<String>
    )

    data class CountryInfo(
        val code: String,
        val name: String,
        val dialCode: String,
        val currency: String,
        val departments: List<DepartmentInfo>
    ) {
        val allCities: List<String> get() = departments.flatMap { it.cities }.distinct()
    }

    val defaultCategories = listOf(
        CategoryItem("cat_plumbing", "Plomberie", "plomberie", "Dépannage canalisation, installation sanitaire, robinetterie", "plumbing", true, 1),
        CategoryItem("cat_electricity", "Électricité", "electricite", "Dépannage électrique, pose de compteurs, câblage bâtiment", "electric_bolt", true, 2),
        CategoryItem("cat_mechanic", "Mécanique auto & moto", "mecanique-auto-moto", "Entretien moteur, vidange, diagnostic électronique, tôlerie", "build", true, 3),
        CategoryItem("cat_repair_tech", "Réparation téléphones & PC", "reparation-telephones-ordinateurs", "Écrans cassés, déblocage, micro-soudure, maintenance PC", "smartphone", true, 4),
        CategoryItem("cat_hair_barber", "Coiffure & Barbier", "coiffure-barbier", "Tresses afro, coupes hommes modernes, locks, soins capillaires", "content_cut", true, 5),
        CategoryItem("cat_fashion", "Couture & Mode", "couture-mode", "Tenues traditionnelles africaines, broderies, retouches sur mesure", "style", true, 6),
        CategoryItem("cat_cleaning", "Nettoyage & Entretien", "nettoyage-entretien", "Nettoyage après chantier, bureaux, maisons et désinfection", "cleaning_services", true, 7),
        CategoryItem("cat_painting", "Peinture & Bâtiment", "peinture-batiment", "Peinture intérieure et extérieure, staff, finitions modernes", "format_paint", true, 8),
        CategoryItem("cat_carpentry", "Menuiserie", "menuiserie", "Meubles en bois massif, portes, placards et aménagement", "carpenter", true, 9),
        CategoryItem("cat_photo_video", "Photographie & Vidéo", "photographie-video", "Shooting studio, mariages, spots publicitaires, captation drones", "photo_camera", true, 10),
        CategoryItem("cat_events", "Décoration & Événementiel", "decoration-evenementiel", "Organisation de cérémonies, bâches, sono, décoration florale", "celebration", true, 11),
        CategoryItem("cat_transport", "Transport & Déménagement", "transport-demenagement", "Camions de déménagement, transport de marchandises inter-villes", "local_shipping", true, 12),
        CategoryItem("cat_delivery", "Livraison", "livraison", "Coursiers à moto, livraison express de plis et colis urgents", "delivery_dining", true, 13),
        CategoryItem("cat_tutoring", "Cours & Formation", "cours-particuliers-formation", "Soutien scolaire (maths, sciences), anglais, bureautique", "school", true, 14),
        CategoryItem("cat_it_support", "Assistance informatique", "assistance-informatique", "Installation réseaux, antivirus, configuration serveurs", "computer", true, 15),
        CategoryItem("cat_translation", "Traduction & Rédaction", "traduction-redaction", "Traduction français/anglais, relecture, documents administratifs", "translate", true, 16),
        CategoryItem("cat_b2b_services", "Services aux entreprises", "services-professionnels-entreprises", "Comptabilité, formalités administratives, domiciliation", "business_center", true, 17),
        CategoryItem("cat_other", "Autres services", "autres-services", "Tous types de services et prestations de proximité", "handyman", true, 18)
    )

    val supportedCountries = listOf(
        CountryInfo("BJ", "Bénin", "+229", "FCFA", listOf(
            DepartmentInfo("Alibori", listOf("Kandi", "Malanville", "Banikoara", "Gogounou", "Karimama", "Ségbana")),
            DepartmentInfo("Atacora", listOf("Natitingou", "Tanguiéta", "Toucountouna", "Kouandé", "Kérou", "Péhunco", "Cobly", "Boukoumbé", "Matéri")),
            DepartmentInfo("Atlantique", listOf("Abomey-Calavi", "Ouidah", "Allada", "Kpomassè", "Toffo", "Tori-Bossito", "Zè", "Sô-Ava")),
            DepartmentInfo("Borgou", listOf("Parakou", "Bembèrèkè", "N'Dali", "Nikki", "Kalalé", "Pèrèrè", "Sinendé", "Tchaourou")),
            DepartmentInfo("Collines", listOf("Dassa-Zoumè", "Savalou", "Glazoué", "Bantè", "Savè", "Ouèssè")),
            DepartmentInfo("Couffo", listOf("Aplahoué", "Djakotomey", "Dogbo", "Klouékanmè", "Lalo", "Toviklin")),
            DepartmentInfo("Donga", listOf("Djougou", "Bassila", "Copargo", "Ouaké")),
            DepartmentInfo("Littoral", listOf("Cotonou")),
            DepartmentInfo("Mono", listOf("Lokossa", "Grand-Popo", "Comè", "Athiémé", "Bopa", "Houéyogbé")),
            DepartmentInfo("Ouémé", listOf("Porto-Novo", "Sèmè-Kpodji", "Adjarra", "Adjohoun", "Aguégués", "Akpro-Missérété", "Avrankou", "Bonou", "Dangbo")),
            DepartmentInfo("Plateau", listOf("Pobè", "Sakété", "Kétou", "Adja-Ouèrè", "Ifangni")),
            DepartmentInfo("Zou", listOf("Bohicon", "Abomey", "Covè", "Za-Kpota", "Zogbodomey", "Djidja", "Agbangnizoun", "Ouinhi", "Zagnanado"))
        )),
        CountryInfo("TG", "Togo", "+228", "FCFA", listOf(
            DepartmentInfo("Maritime", listOf("Lomé", "Tsévié", "Aného", "Vogan", "Tabligbo", "Kévé", "Afagnan")),
            DepartmentInfo("Plateaux", listOf("Kpalimé", "Atakpamé", "Notsé", "Badou", "Amlamé", "Danyi-Apéyémé", "Elavagnon", "Tohoun", "Kougnohou")),
            DepartmentInfo("Centrale", listOf("Sokodé", "Tchamba", "Sotouboua", "Blitta", "Djarkpanga")),
            DepartmentInfo("Kara", listOf("Kara", "Bafilo", "Bassar", "Niamtougou", "Pagouda", "Guérin-Kouka", "Kantè")),
            DepartmentInfo("Savanes", listOf("Dapaong", "Mango", "Cinkassé", "Mandouri", "Tandjouaré"))
        )),
        CountryInfo("CI", "Côte d'Ivoire", "+225", "FCFA", listOf(
            DepartmentInfo("Abidjan", listOf("Abidjan", "Anyama", "Bingerville", "Songon")),
            DepartmentInfo("Yamoussoukro", listOf("Yamoussoukro", "Attégouakro")),
            DepartmentInfo("Gbêkê", listOf("Bouaké", "Béoumi", "Sakassou", "Botro")),
            DepartmentInfo("San-Pédro", listOf("San-Pédro", "Sassandra", "Tabou", "Grabo")),
            DepartmentInfo("Poro", listOf("Korhogo", "Sinématiali", "Dikodougou")),
            DepartmentInfo("Haut-Sassandra", listOf("Daloa", "Issia", "Vavoua", "Zoukougbeu")),
            DepartmentInfo("Indénié-Djuablin", listOf("Abengourou", "Agnibilékrou", "Bettié")),
            DepartmentInfo("Marahoué", listOf("Bouaflé", "Zuénoula", "Sinfra")),
            DepartmentInfo("Gôh", listOf("Gagnoa", "Oumé")),
            DepartmentInfo("Lôh-Djiboua", listOf("Divo", "Guitry", "Lakota")),
            DepartmentInfo("Tonkpi", listOf("Man", "Danané", "Zouan-Hounien", "Biankouma")),
            DepartmentInfo("Sud-Comoé", listOf("Aboisso", "Grand-Bassam", "Adiaké", "Tiapoum")),
            DepartmentInfo("Agnéby-Tiassa", listOf("Agboville", "Tiassalé", "Sikensi", "Taabo")),
            DepartmentInfo("Nawa", listOf("Soubré", "Méagui", "Buyo", "Guéyo")),
            DepartmentInfo("Kabadougou", listOf("Odienné", "Madinani", "Samatiguila")),
            DepartmentInfo("Hambol", listOf("Katiola", "Dabakala", "Niakaramandougou")),
            DepartmentInfo("Gontougo", listOf("Bondoukou", "Tanda", "Transua", "Sandégué")),
            DepartmentInfo("Cavally", listOf("Guiglo", "Toulepleu", "Blolequin", "Taï")),
            DepartmentInfo("Guémon", listOf("Duékoué", "Bangolo", "Kouibly")),
            DepartmentInfo("Bélier", listOf("Toumodi", "Tiébissou", "Didiévi", "Djékanou")),
            DepartmentInfo("Bagoué", listOf("Boundiali", "Tingréla", "Kouto")),
            DepartmentInfo("Tchologo", listOf("Ferkessédougou", "Ouangolodougou", "Kong")),
            DepartmentInfo("Bounkani", listOf("Bouna", "Doropo", "Nassian", "Téhini")),
            DepartmentInfo("Béré", listOf("Mankono", "Dianra", "Kounahiri")),
            DepartmentInfo("Worodougou", listOf("Séguéla", "Kani")),
            DepartmentInfo("Folon", listOf("Minignan", "Kaniasso")),
            DepartmentInfo("Bafing", listOf("Touba", "Koro", "Ouaninou")),
            DepartmentInfo("Moronou", listOf("Bongouanou", "Arrah", "M'Batto")),
            DepartmentInfo("Iffou", listOf("Daoukro", "M'Bahiakro", "Prikro")),
            DepartmentInfo("La Mé", listOf("Adzopé", "Akoupé", "Alépé", "Yakassé-Attobrou")),
            DepartmentInfo("Grands-Ponts", listOf("Dabou", "Grand-Lahou", "Jacqueville"))
        )),
        CountryInfo("SN", "Sénégal", "+221", "FCFA", listOf(
            DepartmentInfo("Dakar", listOf("Dakar", "Pikine", "Guédiawaye", "Rufisque", "Keur Massar")),
            DepartmentInfo("Thiès", listOf("Thiès", "Mbour", "Tivaouane", "Joal-Fadiouth", "Popenguine")),
            DepartmentInfo("Saint-Louis", listOf("Saint-Louis", "Dagana", "Podor", "Richard-Toll")),
            DepartmentInfo("Diourbel", listOf("Touba", "Mbacké", "Diourbel", "Bambey")),
            DepartmentInfo("Fatick", listOf("Fatick", "Foundiougne", "Gossas", "Sokone")),
            DepartmentInfo("Kaolack", listOf("Kaolack", "Nioro du Rip", "Guinguinéo")),
            DepartmentInfo("Kaffrine", listOf("Kaffrine", "Koungheul", "Malem-Hodar", "Birkelane")),
            DepartmentInfo("Louga", listOf("Louga", "Kébémer", "Linguère", "Dahra")),
            DepartmentInfo("Matam", listOf("Matam", "Kanel", "Ranérou", "Ourossogui")),
            DepartmentInfo("Tambacounda", listOf("Tambacounda", "Bakel", "Goudiry", "Koumpentoum")),
            DepartmentInfo("Kédougou", listOf("Kédougou", "Salémata", "Saraya")),
            DepartmentInfo("Kolda", listOf("Kolda", "Vélingara", "Médina Yoro Foulah")),
            DepartmentInfo("Sédhiou", listOf("Sédhiou", "Bounkiling", "Goudomp")),
            DepartmentInfo("Ziguinchor", listOf("Ziguinchor", "Bignona", "Oussouye", "Cap Skirring"))
        )),
        CountryInfo("CM", "Cameroun", "+237", "FCFA", listOf(
            DepartmentInfo("Littoral", listOf("Douala", "Nkongsamba", "Edéa", "Yabassi", "Loum", "Manjo", "Mbanga")),
            DepartmentInfo("Centre", listOf("Yaoundé", "Mbalmayo", "Bafia", "Obala", "Eséka", "Nanga-Eboko", "Akonolinga")),
            DepartmentInfo("Ouest", listOf("Bafoussam", "Foumban", "Dschang", "Bangangté", "Mbouda", "Bandjoun", "Baham")),
            DepartmentInfo("Nord", listOf("Garoua", "Guider", "Pitoa", "Rey-Bouba", "Touboro", "Figuil")),
            DepartmentInfo("Extrême-Nord", listOf("Maroua", "Kousséri", "Yagoua", "Mokolo", "Mora", "Kaélé")),
            DepartmentInfo("Adamaoua", listOf("Ngaoundéré", "Meiganga", "Tibati", "Tignère", "Banyo")),
            DepartmentInfo("Sud", listOf("Ebolowa", "Kribi", "Sangmélima", "Ambam", "Campo")),
            DepartmentInfo("Est", listOf("Bertoua", "Batouri", "Abong-Mbang", "Yokadouma", "Bélabo")),
            DepartmentInfo("Nord-Ouest", listOf("Bamenda", "Kumbo", "Wum", "Nkambé", "Fundong", "Mbengwi")),
            DepartmentInfo("Sud-Ouest", listOf("Buea", "Limbe", "Kumba", "Tiko", "Mamfe", "Mundemba"))
        )),
        CountryInfo("BF", "Burkina Faso", "+226", "FCFA", listOf(
            DepartmentInfo("Centre", listOf("Ouagadougou")),
            DepartmentInfo("Hauts-Bassins", listOf("Bobo-Dioulasso", "Orodara", "Houndé")),
            DepartmentInfo("Nord", listOf("Ouahigouya", "Gourcy", "Titao")),
            DepartmentInfo("Centre-Ouest", listOf("Koudougou", "Réo", "Léo")),
            DepartmentInfo("Centre-Nord", listOf("Kaya", "Kongoussi", "Boulsa")),
            DepartmentInfo("Est", listOf("Fada N'Gourma", "Diapaga", "Bogandé")),
            DepartmentInfo("Boucle du Mouhoun", listOf("Dédougou", "Nouna", "Boromo", "Tougan")),
            DepartmentInfo("Centre-Sud", listOf("Manga", "Kombissiri", "Pô")),
            DepartmentInfo("Sahel", listOf("Dori", "Djibo", "Gorom-Gorom")),
            DepartmentInfo("Cascades", listOf("Banfora", "Sindou")),
            DepartmentInfo("Plateau-Central", listOf("Ziniaré", "Boussé", "Zorgho")),
            DepartmentInfo("Sud-Ouest", listOf("Gaoua", "Dano", "Batié", "Diébougou")),
            DepartmentInfo("Centre-Est", listOf("Tenkodogo", "Koupéla", "Garango", "Pouytenga"))
        )),
        CountryInfo("ML", "Mali", "+223", "FCFA", listOf(
            DepartmentInfo("District de Bamako", listOf("Bamako")),
            DepartmentInfo("Kayes", listOf("Kayes", "Kita", "Bafoulabé", "Nioro du Sahel")),
            DepartmentInfo("Koulikoro", listOf("Koulikoro", "Kati", "Kolokani", "Nara")),
            DepartmentInfo("Sikasso", listOf("Sikasso", "Koutiala", "Bougouni")),
            DepartmentInfo("Ségou", listOf("Ségou", "San", "Niono", "Markala")),
            DepartmentInfo("Mopti", listOf("Mopti", "Sévaré", "Djenné", "Bandiagara")),
            DepartmentInfo("Tombouctou", listOf("Tombouctou", "Niafunké", "Goundam")),
            DepartmentInfo("Gao", listOf("Gao", "Ansongo", "Bourem"))
        )),
        CountryInfo("GN", "Guinée", "+224", "GNF", listOf(
            DepartmentInfo("Conakry", listOf("Conakry", "Kaloum", "Dixinn", "Matam", "Ratoma", "Matoto")),
            DepartmentInfo("Kindia", listOf("Kindia", "Coyah", "Dubréka", "Forécariah", "Télimélé")),
            DepartmentInfo("Boké", listOf("Boké", "Boffa", "Fria", "Gaoual", "Koundara")),
            DepartmentInfo("Mamou", listOf("Mamou", "Dalaba", "Pita")),
            DepartmentInfo("Labé", listOf("Labé", "Mali", "Koubia", "Lélouma", "Tougué")),
            DepartmentInfo("Faranah", listOf("Faranah", "Dinguiraye", "Dabola", "Kissidougou")),
            DepartmentInfo("Kankan", listOf("Kankan", "Siguiri", "Kouroussa", "Mandiana", "Kérouané")),
            DepartmentInfo("Nzérékoré", listOf("Nzérékoré", "Macenta", "Guéckédou", "Beyla", "Lola", "Yomou"))
        )),
        CountryInfo("GA", "Gabon", "+241", "FCFA", listOf(
            DepartmentInfo("Estuaire", listOf("Libreville", "Owendo", "Ntoum", "Kango")),
            DepartmentInfo("Haut-Ogooué", listOf("Franceville", "Moanda", "Mounana", "Bongoville")),
            DepartmentInfo("Moyen-Ogooué", listOf("Lambaréné", "Ndjolé")),
            DepartmentInfo("Ngounié", listOf("Mouila", "Ndendé", "Fougamou", "Mbigou")),
            DepartmentInfo("Nyanga", listOf("Tchibanga", "Mayumba")),
            DepartmentInfo("Ogooué-Ivindo", listOf("Makokou", "Booué")),
            DepartmentInfo("Ogooué-Lolo", listOf("Koulamoutou", "Lastoursville")),
            DepartmentInfo("Ogooué-Maritime", listOf("Port-Gentil", "Gamba", "Omboué")),
            DepartmentInfo("Woleu-Ntem", listOf("Oyem", "Bitam", "Mitzic", "Minvoul"))
        )),
        CountryInfo("CD", "RD Congo", "+243", "CDF", listOf(
            DepartmentInfo("Kinshasa", listOf("Kinshasa")),
            DepartmentInfo("Haut-Katanga", listOf("Lubumbashi", "Likasi", "Kasumbalesa")),
            DepartmentInfo("Nord-Kivu", listOf("Goma", "Beni", "Butembo")),
            DepartmentInfo("Sud-Kivu", listOf("Bukavu", "Uvira")),
            DepartmentInfo("Kongo-Central", listOf("Matadi", "Boma", "Muanda")),
            DepartmentInfo("Tshopo", listOf("Kisangani")),
            DepartmentInfo("Kasaï-Central", listOf("Kananga")),
            DepartmentInfo("Kasaï-Oriental", listOf("Mbuji-Mayi"))
        ))
    )
}
