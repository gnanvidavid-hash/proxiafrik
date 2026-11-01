package com.example.data.local

import java.util.UUID

object SeedData {
    val initialCategories = listOf(
        CategoryEntity("cat_plumbing", "Plomberie", "plomberie", "Dépannage canalisation, installation sanitaire, robinetterie", "plumbing", true, 1),
        CategoryEntity("cat_electricity", "Électricité", "electricite", "Dépannage électrique, pose de compteurs, câblage bâtiment", "electric_bolt", true, 2),
        CategoryEntity("cat_mechanic", "Mécanique auto & moto", "mecanique-auto-moto", "Entretien moteur, vidange, diagnostic électronique, tôlerie", "build", true, 3),
        CategoryEntity("cat_repair_tech", "Réparation téléphones & PC", "reparation-telephones-ordinateurs", "Écrans cassés, déblocage, micro-soudure, maintenance PC", "smartphone", true, 4),
        CategoryEntity("cat_hair_barber", "Coiffure & Barbier", "coiffure-barbier", "Tresses afro, coupes hommes modernes, locks, soins capillaires", "content_cut", true, 5),
        CategoryEntity("cat_fashion", "Couture & Mode", "couture-mode", "Tenues traditionnelles africaines, broderies, retouches sur mesure", "style", true, 6),
        CategoryEntity("cat_cleaning", "Nettoyage & Entretien", "nettoyage-entretien", "Nettoyage après chantier, bureaux, maisons et désinfection", "cleaning_services", true, 7),
        CategoryEntity("cat_painting", "Peinture & Bâtiment", "peinture-batiment", "Peinture intérieure et extérieure, staff, finitions modernes", "format_paint", true, 8),
        CategoryEntity("cat_carpentry", "Menuiserie", "menuiserie", "Meubles en bois massif, portes, placards et aménagement", "carpenter", true, 9),
        CategoryEntity("cat_photo_video", "Photographie & Vidéo", "photographie-video", "Shooting studio, mariages, spots publicitaires, captation drones", "photo_camera", true, 10),
        CategoryEntity("cat_events", "Décoration & Événementiel", "decoration-evenementiel", "Organisation de cérémonies, bâches, sono, décoration florale", "celebration", true, 11),
        CategoryEntity("cat_transport", "Transport & Déménagement", "transport-demenagement", "Camions de déménagement, transport de marchandises inter-villes", "local_shipping", true, 12),
        CategoryEntity("cat_delivery", "Livraison", "livraison", "Coursiers à moto, livraison express de plis et colis urgents", "delivery_dining", true, 13),
        CategoryEntity("cat_tutoring", "Cours particuliers & Formation", "cours-particuliers-formation", "Soutien scolaire (maths, sciences), anglais, bureautique", "school", true, 14),
        CategoryEntity("cat_it_support", "Assistance informatique", "assistance-informatique", "Installation réseaux, antivirus, configuration serveurs", "computer", true, 15),
        CategoryEntity("cat_translation", "Traduction & Rédaction", "traduction-redaction", "Traduction français/anglais, relecture, documents administratifs", "translate", true, 16),
        CategoryEntity("cat_b2b_services", "Services aux entreprises", "services-professionnels-entreprises", "Comptabilité, formalités administratives, domiciliation", "business_center", true, 17),
        CategoryEntity("cat_other", "Autres services", "autres-services", "Tous types de services et prestations de proximité", "handyman", true, 18)
    )

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

    suspend fun populateInitialData(db: AppDatabase) {
        val catDao = db.categoryDao()
        val userDao = db.userDao()
        val proDao = db.professionalProfileDao()
        val serviceDao = db.serviceDao()
        val portfolioDao = db.portfolioDao()
        val reviewDao = db.reviewDao()
        val subDao = db.subscriptionDao()
        val auditDao = db.adminAuditLogDao()

        // 1. Categories
        catDao.insertAll(initialCategories)

        // 2. Initial Users
        val adminUser = UserEntity(
            id = "user_admin_01",
            fullName = "Administrateur ProxiAfrik",
            email = "admin@proxiafrik.com",
            phone = "+22997999999",
            role = "ADMIN"
        )
        val clientUser = UserEntity(
            id = "user_client_01",
            fullName = "Aïcha Dossou",
            email = "client@proxiafrik.com",
            phone = "+22997000001",
            role = "CLIENT"
        )
        val pro1User = UserEntity(
            id = "user_pro_01",
            fullName = "Jean-Paul Mensah",
            email = "pro@proxiafrik.com",
            phone = "+22997112233",
            role = "PROFESSIONAL"
        )
        val pro2User = UserEntity(
            id = "user_pro_02",
            fullName = "Koffi Assogba",
            email = "koffi@proxiafrik.com",
            phone = "+22890123456",
            role = "PROFESSIONAL"
        )
        val pro3User = UserEntity(
            id = "user_pro_03",
            fullName = "Fatou Diallo",
            email = "fatou@proxiafrik.com",
            phone = "+221771234567",
            role = "PROFESSIONAL"
        )
        val proPendingUser = UserEntity(
            id = "user_pro_pending",
            fullName = "Amadou Traoré",
            email = "amadou@proxiafrik.com",
            phone = "+2250708091011",
            role = "PROFESSIONAL"
        )

        userDao.insertUser(adminUser)
        userDao.insertUser(clientUser)
        userDao.insertUser(pro1User)
        userDao.insertUser(pro2User)
        userDao.insertUser(pro3User)
        userDao.insertUser(proPendingUser)

        // 3. Professional Profiles
        // Profile 1: Plomberie Express Bénin (APPROVED)
        val pro1 = ProfessionalProfileEntity(
            id = "pro_01",
            userId = pro1User.id,
            businessName = "Plomberie Express & Sanitaire",
            slug = "plomberie-express-sanitaire",
            description = "Artisan plombier qualifié avec 12 ans d'expérience. Dépannage rapide de fuites, débouchage de canalisations, installation chauffe-eau et raccordement sanitaire pour particuliers et entreprises.",
            countryCode = "BJ",
            countryName = "Bénin",
            department = "Littoral",
            city = "Cotonou",
            neighborhood = "Cadjehoun",
            interventionZones = "Cotonou, Fidjrossè, Akpakpa, Abomey-Calavi",
            phonePublic = "+22997112233",
            whatsappNumber = "+22997112233",
            primaryCategoryId = "cat_plumbing",
            verificationStatus = "APPROVED",
            publicationStatus = "PUBLISHED",
            openingHours = "Lun - Sam : 07h30 - 19h00 (Urgences 24/7)",
            viewsCount = 142,
            isSponsored = true
        )

        // Profile 2: Électricité Moderne Togo (APPROVED)
        val pro2 = ProfessionalProfileEntity(
            id = "pro_02",
            userId = pro2User.id,
            businessName = "Koffi Élec & Énergie Solaire",
            slug = "koffi-elec-energie-solaire",
            description = "Spécialiste en électricité générale, installation de kits solaires résidentiels, dépannage de tableaux électriques et protection contre les surtensions.",
            countryCode = "TG",
            countryName = "Togo",
            department = "Maritime",
            city = "Lomé",
            neighborhood = "Bè-Kpota",
            interventionZones = "Grand Lomé, Baguida, Agoè-Nyivé",
            phonePublic = "+22890123456",
            whatsappNumber = "+22890123456",
            primaryCategoryId = "cat_electricity",
            verificationStatus = "APPROVED",
            publicationStatus = "PUBLISHED",
            openingHours = "Lun - Ven : 08h00 - 18h00",
            viewsCount = 89,
            isSponsored = false
        )

        // Profile 3: Couture & Création Fatou Dakar (APPROVED)
        val pro3 = ProfessionalProfileEntity(
            id = "pro_03",
            userId = pro3User.id,
            businessName = "Atelier Couture Élegance Dakar",
            slug = "atelier-couture-elegance-dakar",
            description = "Maison de couture spécialisée dans la confection de tenues traditionnelles ou modernes : boubous en basin riche brodé, tailleurs et tenues de fête sur mesure.",
            countryCode = "SN",
            countryName = "Sénégal",
            department = "Dakar",
            city = "Dakar",
            neighborhood = "Almadies",
            interventionZones = "Dakar Plateau, Almadies, Mermoz, Yoff",
            phonePublic = "+221771234567",
            whatsappNumber = "+221771234567",
            primaryCategoryId = "cat_fashion",
            verificationStatus = "APPROVED",
            publicationStatus = "PUBLISHED",
            openingHours = "Mar - Sam : 09h00 - 19h00",
            viewsCount = 210,
            isSponsored = true
        )

        // Profile 4: Menuiserie Atlantique Calavi (APPROVED)
        val pro4User = UserEntity(
            id = "user_pro_04",
            fullName = "Anicet Hounnou",
            email = "anicet@proxiafrik.com",
            phone = "+22996445566",
            role = "PROFESSIONAL"
        )
        userDao.insertUser(pro4User)

        val pro4 = ProfessionalProfileEntity(
            id = "pro_04",
            userId = pro4User.id,
            businessName = "Menuiserie Bois & Aluminium Atlantique",
            slug = "menuiserie-atlantique-calavi",
            description = "Atelier de fabrication de meubles sur mesure, portes isoplanes, dressings et menuiserie aluminium pour habitat et bureaux.",
            countryCode = "BJ",
            countryName = "Bénin",
            department = "Atlantique",
            city = "Abomey-Calavi",
            neighborhood = "Godomey",
            interventionZones = "Abomey-Calavi, Cotonou, Ouidah, Akassato",
            phonePublic = "+22996445566",
            whatsappNumber = "+22996445566",
            primaryCategoryId = "cat_carpentry",
            verificationStatus = "APPROVED",
            publicationStatus = "PUBLISHED",
            openingHours = "Lun - Sam : 08h00 - 18h30",
            viewsCount = 76,
            isSponsored = false
        )

        // Profile 5: Réparation Téléphones & Informatique Abidjan (PENDING for admin review CUJ)
        val proPending = ProfessionalProfileEntity(
            id = "pro_pending_01",
            userId = proPendingUser.id,
            businessName = "Ivoire Tech Fix & Micro-soudure",
            slug = "ivoire-tech-fix",
            description = "Réparation rapide de smartphones toutes marques (iPhone, Samsung, Tecno), remplacement d'écrans tactiles, désoxydation liquide et réinstallation système.",
            countryCode = "CI",
            countryName = "Côte d'Ivoire",
            department = "Abidjan",
            city = "Abidjan",
            neighborhood = "Cocody Angré",
            interventionZones = "Cocody, Plateau, Marcory, Yopougon",
            phonePublic = "+2250708091011",
            whatsappNumber = "+2250708091011",
            primaryCategoryId = "cat_repair_tech",
            verificationStatus = "PENDING",
            publicationStatus = "PUBLISHED",
            openingHours = "Lun - Sam : 08h30 - 19h30",
            viewsCount = 12,
            isSponsored = false
        )

        proDao.insertProfile(pro1)
        proDao.insertProfile(pro2)
        proDao.insertProfile(pro3)
        proDao.insertProfile(pro4)
        proDao.insertProfile(proPending)

        // 4. Services
        val s1 = ServiceEntity("srv_101", pro1.id, "cat_plumbing", "Débouchage urgent de canalisation", "Désobstruction mécanique rapide des éviers, siphons et WC.", 15000.0, "FCFA", "STARTING_AT")
        val s2 = ServiceEntity("srv_102", pro1.id, "cat_plumbing", "Remplacement robinetterie et mitigeur", "Pose de mitigeurs fournis ou achetés, garantie d'étanchéité.", 8000.0, "FCFA", "FIXED")
        val s3 = ServiceEntity("srv_103", pro1.id, "cat_plumbing", "Installation complète réseau d'eau", "Pose tuyauterie multicouche pour maison neuve ou rénovation.", 0.0, "FCFA", "SUR_DEVIS")

        val s4 = ServiceEntity("srv_201", pro2.id, "cat_electricity", "Installation kit solaire autonome", "Dimensionnement et montage panneau solaire + onduleur + batterie.", 0.0, "FCFA", "SUR_DEVIS")
        val s5 = ServiceEntity("srv_202", pro2.id, "cat_electricity", "Dépannage panne tableau électrique", "Recherche de court-circuit et mise en conformité disjoncteur.", 12000.0, "FCFA", "STARTING_AT")

        val s6 = ServiceEntity("srv_301", pro3.id, "cat_fashion", "Confection Boubou Grand Modèle", "Couture artisanale sur basin ou brodé avec finitions minutieuses.", 35000.0, "FCFA", "STARTING_AT")
        val s7 = ServiceEntity("srv_302", pro3.id, "cat_fashion", "Retouches et ajustements express", "Ajustement de taille, ourlets et réparations d'urgence en 24h.", 5000.0, "FCFA", "FIXED")

        val s8 = ServiceEntity("srv_401", proPending.id, "cat_repair_tech", "Remplacement écran smartphone", "Changement dalle LCD / OLED garantie pièces testées.", 20000.0, "FCFA", "STARTING_AT")
        val s9 = ServiceEntity("srv_501", pro4.id, "cat_carpentry", "Fabrication meuble TV & dressing sur mesure", "Conception 3D, bois massif ou mélaminé haute densité.", 85000.0, "FCFA", "STARTING_AT")
        val s10 = ServiceEntity("srv_502", pro4.id, "cat_carpentry", "Pose baie vitrée aluminium et moustiquaire", "Profilés aluminium thermolaqués, étanchéité garantie.", 45000.0, "FCFA", "FIXED")

        serviceDao.insertAll(listOf(s1, s2, s3, s4, s5, s6, s7, s8, s9, s10))

        // 5. Portfolio Images (Chantiers et réalisations concrètes)
        val img1 = PortfolioImageEntity("img_1", pro1.id, "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=600", "Rénovation complète d'une salle de bain à Cadjehoun (carrelage & plomberie)")
        val img2 = PortfolioImageEntity("img_2", pro1.id, "https://images.unsplash.com/photo-1504148455328-c376907d081c?w=600", "Raccordement compteur et nourrice multicouche avec vannes d'arrêt individuelles")
        val img3 = PortfolioImageEntity("img_3", pro1.id, "https://images.unsplash.com/photo-1585704032915-c3400ca199e7?w=600", "Pose d'un surpresseur automatique et cuve de réserve d'eau 1000L")
        val img4 = PortfolioImageEntity("img_4", pro1.id, "https://images.unsplash.com/photo-1621905251189-08b45d6a269e?w=600", "Installation chauffe-eau électrique avec groupe de sécurité neuf")

        val img5 = PortfolioImageEntity("img_5", pro2.id, "https://images.unsplash.com/photo-1509391365360-2e959784a276?w=600", "Installation centrale solaire hybride 5kVA avec 8 panneaux à Lomé")
        val img6 = PortfolioImageEntity("img_6", pro2.id, "https://images.unsplash.com/photo-1558441719-7553b47c617b?w=600", "Armoire électrique tertiaire aux normes avec disjoncteurs différentiels")
        val img7 = PortfolioImageEntity("img_7", pro2.id, "https://images.unsplash.com/photo-1513694203232-719a280e022f?w=600", "Raccordement banc de batteries Lithium 48V de secours")

        val img8 = PortfolioImageEntity("img_8", pro3.id, "https://images.unsplash.com/photo-1558769132-cb1aea458c5e?w=600", "Boubou traditionnel en basin riche teinté main avec broderie dorée")
        val img9 = PortfolioImageEntity("img_9", pro3.id, "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=600", "Tailleuse moderne deux pièces pour cérémonie de mariage aux Almadies")
        val img10 = PortfolioImageEntity("img_10", pro3.id, "https://images.unsplash.com/photo-1490481651871-ab68de25d43d?w=600", "Ensemble d'apparat en pagne tissé traditionnel et finitions soignées")

        val img11 = PortfolioImageEntity("img_11", pro4.id, "https://images.unsplash.com/photo-1538688525198-9b88f6f53126?w=600", "Dressing moderne sur mesure en bois teck verni à Godomey")
        val img12 = PortfolioImageEntity("img_12", pro4.id, "https://images.unsplash.com/photo-1513694203232-719a280e022f?w=600", "Baie vitrée coulissante en aluminium laqué blanc double vitrage")

        portfolioDao.insertAll(listOf(img1, img2, img3, img4, img5, img6, img7, img8, img9, img10, img11, img12))

        // 6. Subscriptions
        subDao.insertSubscription(SubscriptionEntity("sub_1", pro1.id, "PRO", "Offre Pro", status = "ACTIVE"))
        subDao.insertSubscription(SubscriptionEntity("sub_2", pro2.id, "FREE", "Offre Gratuite", status = "ACTIVE"))
        subDao.insertSubscription(SubscriptionEntity("sub_3", pro3.id, "VISIBILITY", "Offre Visibilité", status = "ACTIVE"))
        subDao.insertSubscription(SubscriptionEntity("sub_4", proPending.id, "FREE", "Offre Gratuite", status = "ACTIVE"))

        // 7. Initial Reviews (moderated & approved customer ratings)
        val r1 = ReviewEntity("rev_1", pro1.id, clientUser.id, "Aïcha D.", 5, "Intervention rapide en moins d'une heure pour une fuite d'eau à Cadjehoun. Très professionnel !", "APPROVED", System.currentTimeMillis() - 86400000L * 2)
        val r2 = ReviewEntity("rev_2", pro1.id, "user_ext_01", "Gérard Houndégbé", 5, "Pose complète de la plomberie pour ma nouvelle maison à Calavi. Travail soigné et devis parfaitement respecté.", "APPROVED", System.currentTimeMillis() - 86400000L * 5)
        val r3 = ReviewEntity("rev_3", pro1.id, "user_ext_02", "Marcelle K.", 4, "Très bon travail pour le remplacement du chauffe-eau. Ponctuel et donne de bons conseils d'entretien.", "APPROVED", System.currentTimeMillis() - 86400000L * 10)
        val r4 = ReviewEntity("rev_4", pro1.id, "user_ext_03", "Drissa B.", 5, "Débouchage express un dimanche soir. Disponibilité exemplaire et tarif honnête.", "APPROVED", System.currentTimeMillis() - 86400000L * 14)

        val r5 = ReviewEntity("rev_5", pro2.id, "user_ext_04", "Komi Agbégnigan", 5, "Installation solaire 5kVA au top ! Plus aucune coupure de courant depuis 6 mois. M. Koffi maîtrise parfaitement son domaine.", "APPROVED", System.currentTimeMillis() - 86400000L * 3)
        val r6 = ReviewEntity("rev_6", pro2.id, "user_ext_05", "Afi Mensah", 5, "Dépannage d'urgence sur notre tableau principal. Diagnostic rapide et sécurité assurée.", "APPROVED", System.currentTimeMillis() - 86400000L * 7)
        val r7 = ReviewEntity("rev_7", pro2.id, "user_ext_06", "Yao Edoh", 4, "Bonne prestation pour la pose des panneaux solaires. Équipe dynamique et polie.", "APPROVED", System.currentTimeMillis() - 86400000L * 12)

        val r8 = ReviewEntity("rev_8", pro3.id, "user_ext_07", "Mireille S.", 5, "Tenue parfaite pour le baptême de mon neveu. Finitions impeccables et coupe moderne !", "APPROVED", System.currentTimeMillis() - 86400000L * 4)
        val r9 = ReviewEntity("rev_9", pro3.id, "user_ext_08", "Coumba Ndiaye", 5, "La meilleure couturière des Almadies. Délais respectés au jour près pour la fête.", "APPROVED", System.currentTimeMillis() - 86400000L * 8)
        val r10 = ReviewEntity("rev_10", pro3.id, "user_ext_09", "Ousmane S.", 5, "Boubou grand modèle brodé avec une grande finesse. Je recommande vivement l'atelier !", "APPROVED", System.currentTimeMillis() - 86400000L * 15)

        val r11 = ReviewEntity("rev_11", pro4.id, "user_ext_10", "Christian Dossou", 5, "Meubles sur mesure et dressing impeccables, le bois teck est de premier choix.", "APPROVED", System.currentTimeMillis() - 86400000L * 6)
        val r12 = ReviewEntity("rev_12", pro4.id, "user_ext_11", "Bernadette A.", 4, "Pose des baies vitrées aluminium propre et soignée. Bon rapport qualité-prix.", "APPROVED", System.currentTimeMillis() - 86400000L * 11)

        reviewDao.insertAll(listOf(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12))

        // 8. Audit log initial
        auditDao.insertLog(AdminAuditLogEntity(
            id = "audit_01",
            adminUserId = adminUser.id,
            action = "APPROVE_PROFILE",
            targetType = "PROFESSIONAL_PROFILE",
            targetId = pro1.id,
            details = "Validation initiale du profil Plomberie Express après vérification des pièces.",
            createdAt = System.currentTimeMillis() - 86400000L
        ))
    }
}
