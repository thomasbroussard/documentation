# Prise en main des API Wepak (avec PHP7)

* Date: 2018-01-31
* Mise à jour: 2018-05-31

## Présentation de la brique

### Principe de la brique PHP 7

Pour chaque API du service, il y a une méthode d'appel à cette API dans la classe \wellpack\wepak\WepackService. Cette méthode reçoit en paramètre un objet de requête spécifique à l'API et retournera un objet de réponse lui aussi spécifique à l'API du service.


### Création d'une application simple

Une fois dézippée la brique PHP 7 :

* Créer un dossier « app » Dans le dossier de déploiement de votre serveur web.
* Créer un dossier « vendor » dans le dossier « app ».
* Copier le dossier « wellpack » dans ce dossier « vendor ».
* Copier le fichier « SplClassLoader.php » à la racine de « app ». (Vous pouvez évidemment utiliser votre propre classLoader)
* Créer un fichier « index.php » à la racine de « app ».

### Charger la librairie d'interrogation du service Rest

    // Chemin du dossier courant
    $CURRENT_DIR = __DIR__ . DIRECTORY_SEPARATOR;
    // Chemin des dépendances
    $VENDOR_DIR = $CURRENT_DIR . DIRECTORY_SEPARATOR . 'vendor' . DIRECTORY_SEPARATOR;
     
    // Charger le classLoader
    include_once($CURRENT_DIR . 'SplClassLoader.php');
    
    // instancier le classloader
    $classloader = new SplClassLoader();
    // Enregistrer le classloader
    $classloader->register();
    // Déclarer l'epsace de nom « wellpack »
    $classloader->addNamespace('wellpack', $VENDOR_DIR . 'wellpack');

### Instancier le service
    
    // Clé d'utilisation
    $API_KEY = 'd5a8237f-0286-11e8-987b-000d3a2457dd';
    
    // URL d'appel du service
    $URL = 'https://preprod.wepak.wellpack.fr/smsenvoi.php';
    
    /**
     * @var \wellpack\wepak\WepackService
     */
    $service = new \wellpack\wepak\WepackService($API_KEY, $URL);


## Appel des services

Les différents services proposés vous permettent :
* 2.1. Api "volume global" : d'obtenir le volume à jour global de données disponibles
* 2.2. Api "volume" : d'obtenir un volume en fonction de critères simples de sélection
* 2.3. Api "send" : d'envoyer une campagne de sms 
* 2.4. Api "send_test" : d'envoyer un sms test préalablement à la généralisation de la campagne
* 2.5. Api calcul par groupe de localité : API complète regroupant l'ensemble des services proposés par Wepak.fr  (sélection complexe, programmation de campagne).

Les deux prochaines api permettent de configurer les groupes de centres d'intérêt
* 2.6. Api de récupération de la liste des groupes d’intérêts
* 2.7. Api de récupération de la liste des ci/qualif associé aux groupes d’intérêts

Les trois prochaines api permettent la gestion des liens courts dans vos messages.
* 2.8. Api de création de lien court
* 2.9. Api de listage des liens courts
* 2.10. Api de suppression d'un lien court


### Api "volume global"


=> Créer une requête VolumeRequest

    /**
     * @var \wellpack\wepak\VolumeGlobalRequest
     */
    $request = new \wellpack\wepak\VolumeGlobalRequest();

=> Envoyer la requête et récupérer une réponse spécifique. Ici ce sera une instance d'objet `\wellpack\wepak\VolumeGlobalResponse`.

    /**
     * @var \wellpack\wepak\VolumeGlobalResponse $response
     */
    $response = $service->getVolumeGlobal($request);

=> Vérifier que la réponse est valide. Si ce n'est pas le cas, lire le message d'erreur.

    if (!$response->isValid()) {
        $error = $response->getErrorMessage();
        // Do something with message
        return;
    }

=> Récupérer les informations contenues dans la réponse

    // Valeur du volume global
    $value = $response->getVolumeGlobal();


### Api "volume"

=> Créer une requête VolumeRequest

    /**
     * @var \wellpack\wepak\VolumeRequest
     */
    $request = new \wellpack\wepak\VolumeRequest();

=> Passer ensuite les paramètres de filtrage de la requête :

    $request->setCodePostal(75014);
    $request->setDept(75);
    $request->setCivilite(\wellpack\wepak\ServiceRequest::CIVILITE_HOMME);
    $request->setAgemin(30);
    $request->setAgemax(45);

=> Envoyer la requête et récupérer une réponse spécifique. Ici ce sera une instance d'objet `\wellpack\wepak\VolumeResponse`.

    /**
     * @var \wellpack\wepak\VolumeResponse $response
     */
    $response = $service->getVolume($request);

=> Vérifier que la réponse est valide. Si ce n'est pas le cas, lire le message d'erreur.

    if (!$response->isValid()) {
        $error = $response->getErrorMessage();
        // Do something with message
        return;
    }

=> Récupérer les informations contenues dans la réponse

    // Valeur du volume
    $value = $response->getVolume();


### Api "send"

=> Créer une requête SendRequest

    /**
     * @var \wellpack\wepak\SendRequest
     */
    $request = new \wellpack\wepak\SendRequest();

=> Passer ensuite les paramètres de filtrage de la requête :

    $request->setQuantite(1000);
    $request->setCodePostal('75015, 75014');
    $request->setDept('75,33');
    $request->setCivilite(\wellpack\wepak\ServiceRequest::CIVILITE_HOMME);
    $request->setAgemin(30);
    $request->setAgemax(45);
    $request->setNumeroCommercant('06xxxxxxxx');
    $request->setContent('text du sms');
    $request->setSenderlabel('nom emeteur');
    $request->setSendDate('2015-08-12');
    $request->setTimeSend('16:30:00');
    $request->setImmediateSend(false);
    $request->setNumPartner('0612345678, 0623456789');

=> Envoyer la requête et récupérer une réponse spécifique. Ici ce sera une instance d'objet `\wellpack\wepak\SendResponse`.

    /**
     * @var \wellpack\wepak\SendResponse $response
     */
    $response = $service->getSend($request);

=> Vérifier que la réponse est valide. Si ce n'est pas le cas, lire le message d'erreur.

    if (!$response->isValid()) {
        $error = $response->getErrorMessage();
        // Do something with message
        return;
    }

=> Vérifier que la réponse est un succès. Sinon, lire le message d'erreur

    if (!$response->isSuccess()) {
        $message = $response->getMessage();
        // Do something with message
        return;
    }

=> Récupérer les informations contenues dans la réponse

    // Message retourné
    $message = $response->getMessage();


### Api "send_test"

=> Créer une requête SendTestRequest

    /**
     * @var \wellpack\wepak\SendTestRequest
     */
    $request = new \wellpack\wepak\SendTestRequest();

=> Passer ensuite les paramètres de filtrage de la requête :

    $request->setNumeroCommercant('06xxxxxxx');
    $request->setContent('text du sms');
    $request->setSenderlabel('nom éméteur');

=> Envoyer la requête et récupérer une réponse spécifique. Ici ce sera une instance d'objet `\wellpack\wepak\SendTestResponse`.

    /**
     * @var \wellpack\wepak\SendTestResponse $response
     */
    $response = $service->getSendTest($request);

=> Vérifier que la réponse est valide. Si ce n'est pas le cas, lire le message d'erreur.

    if (!$response->isValid()) {
        $error = $response->getErrorMessage();
        // Do something with message
        return;
    }

=> Vérifier que la réponse est un succès. Sinon, lire le message d'erreur

    if (!$response->isSuccess()) {
        $message = $response->getMessage();
        // Do something with message
        return;
    }

=> Récupérer les informations contenues dans la réponse

    // Message retourné
    $message = $response->getMessage();

### Api calcul par groupe de localité

=> Créer une requête LocalityGroupCalcRequest

    /**
     * @var \wellpack\wepak\LocalityGroupCalcRequest
     */
    $request = new \wellpack\wepak\LocalityGroupCalcRequest();

=> Passer ensuite les paramètres de filtrage de la requête :

    use \wellpack\wepak\ServiceRequest;
    
    $request->setQuantite(2600);
    $request->setListeCpDept([
        [
            ServiceRequest::KEY_LABEL => '75015',
            ServiceRequest::KEY_CP => [75015, 75014, 75013, 75016],
            ServiceRequest::KEY_QUANTITE => 500
        ], [
            ServiceRequest::KEY_LABEL => '33000',
            ServiceRequest::KEY_CP => [33000, 33100, 33200],
            ServiceRequest::KEY_QUANTITE => 600
        ], [
            ServiceRequest::KEY_LABEL => '75',
            ServiceRequest::KEY_CP => [75, 92, 91],
            ServiceRequest::KEY_QUANTITE => 700
        ], [
            ServiceRequest::KEY_LABEL => '33',
            ServiceRequest::KEY_CP => [33, 40, 42],
            ServiceRequest::KEY_QUANTITE => 800
        ]
    ]);
    $request->setCivilite(\wellpack\wepak\ServiceRequest::CIVILITE_HOMME);
    $request->setAgemax('45');
    $request->setAgemin('30');
    // "ci":"4"
    $request->setNumeroCommercant('06xxxxxxxx');
    //"doublon":"true"
    $request->setDataout('volume');

=> Envoyer la requête et récupérer une réponse spécifique. Ici ce sera une instance d'objet `\wellpack\wepak\LocalityGroupCalcResponse`.

    /**
     * @var \wellpack\wepak\LocalityGroupCalcResponse $response
     */
    $response = $service->getLocalityGroupCalc($request);

=> Vérifier que la réponse est valide. Si ce n'est pas le cas, lire le message d'erreur.

    if (!$response->isValid()) {
        $error = $response->getErrorMessage();
        // Do something with message
        return;
    }

=> Récupérer les informations contenues dans la réponse

    // Liste des localités quand dataout = "volume"
    $localities = $response->getLocalitiesData();
    // Parcourir chaque localité et récupérer les informations
    foreach ($localities as $locality) {
        $type = $locality->getType();
        $loc = $locality->getLocality();
        $vol = $locality->getVolume();
    }
    
    // requête quand dataout = "txt"
    $req = $response->getRequest();
    
    // Liste quand dataout = "liste"
    $list = $response->getList();
    // Parcourir la liste
    foreach ($list as $codePostal => $listNumeros) {
        // $listNumeros : liste des numéros
    }


### Api de récupération de la liste des groupes d’intérêts

=> Créer une requête ListInterestGroupRequest

    /**
     * @var \wellpack\wepak\ListInterestGroupRequest
     */
    $request = new \wellpack\wepak\ListInterestGroupRequest();

=> Passer ensuite les paramètres de filtrage de la requête :

    $request->setIdInterestGroup(26);

=> Envoyer la requête et récupérer une réponse spécifique. Ici ce sera une instance d'objet `\wellpack\wepak\ListInterestGroupResponse`.

    /**
     * @var \wellpack\wepak\ListInterestGroupResponse $response
     */
    $response = $service->getListInterestGroup($request);

=> Vérifier que la réponse est valide. Si ce n'est pas le cas, lire le message d'erreur.

    if (!$response->isValid()) {
        $error = $response->getErrorMessage();
        // Do something with message
        return;
    }

=> Récupérer les informations contenues dans la réponse

    // Liste des groupes d’intérêt père/fils configurer sur le site web
    $list = $response->getListInterestGroup();
    // Parcourir la liste
    foreach ($list as $group) {
        $id = $group['id_interest_group'];
        $label = $group['label'];
        $description = $group['description'];
    }

### Api de récupération de la liste des ci/qualif associés aux groupes d’intérêts

=> Créer une requête GetInteretValueRequest

    /**
     * @var \wellpack\wepak\GetInteretValueRequest
     */
    $request = new \wellpack\wepak\GetInteretValueRequest();

=> Passer ensuite les paramètres de filtrage de la requête :

    $request->setIdInterestGroup(26);

=> Envoyer la requête et récupérer une réponse spécifique. Ici ce sera une instance d'objet `\wellpack\wepak\GetInteretValueResponse`.

    /**
     * @var \wellpack\wepak\GetInteretValueResponse $response
     */
    $response = $service->getInteretValue($request);

=> Vérifier que la réponse est valide. Si ce n'est pas le cas, lire le message d'erreur.

    if (!$response->isValid()) {
        $error = $response->getErrorMessage();
        // Do something with message
        return;
    }

=> Récupérer les informations contenues dans la réponse

    // Liste des intérêts associés au groupe d’intérêt.
    $list = $response->getInteretValueList();
    // Parcourir la liste
    foreach ($list as $group) {
        $wellpack_id = $group['wellpack_id'];
        $type = $group['type'];
        $label = $group['label'];
    }

### Api de création de lien court

=> Créer une requête CreateLienCourtRequest

    /**
     * @var \wellpack\wepak\CreateLienCourtRequest
     */
    $request = new \wellpack\wepak\CreateLienCourtRequest();

=> Passer ensuite les paramètres de filtrage de la requête :

    $request->setKey(1);
    $request->setAdresseCible('http://wepak.fr');
    $request->setExtension('promo02');
    $request->setNomLienCourt('lien wellpack');

=> Envoyer la requête et récupérer une réponse spécifique. Ici ce sera une instance d'objet `\wellpack\wepak\CreateLienCourtResponse`.

    /**
     * @var \wellpack\wepak\CreateLienCourtResponse $response
     */
    $response = $service->getCreateLienCourt($request);

=> Vérifier que la réponse est valide. Si ce n'est pas le cas, lire le message d'erreur.

    if (!$response->isValid()) {
        $error = $response->getErrorMessage();
        // Do something with message
        return;
    }

=> Vérifier que la réponse est un succès. Sinon, lire le message d'erreur

    if (!$response->isSuccess()) {
        $message = $response->getMessage();
        // Do something with message
        return;
    }

=> Récupérer les informations contenues dans la réponse

    $idLienCourt = $response->getIDLienCourt();
    $lienCourt = $response->getLienCourt();


### Api de listage des liens courts

=> Créer une requête ListLienCourtRequest

    /**
     * @var \wellpack\wepak\ListLienCourtRequest
     */
    $request = new \wellpack\wepak\ListLienCourtRequest();

=> Passer ensuite les paramètres de filtrage de la requête :

    $request->setKey(1);

=> Envoyer la requête et récupérer une réponse spécifique. Ici ce sera une instance d'objet `\wellpack\wepak\ListLienCourtResponse`.

    /**
     * @var \wellpack\wepak\ListLienCourtResponse $response
     */
    $response = $service->getListLienCourt($request);

=> Vérifier que la réponse est valide. Si ce n'est pas le cas, lire le message d'erreur.

    if (!$response->isValid()) {
        $error = $response->getErrorMessage();
        // Do something with message
        return;
    }

=> Vérifier que la réponse est un succès. Sinon, lire le message d'erreur

    if (!$response->isSuccess()) {
        $message = $response->getMessage();
        // Do something with message
        return;
    }

=> Récupérer les informations contenues dans la réponse

    // Liste des liens courts
    $liste = $response->getListLienCourt();
    // Parcourir la liste
    foreach($liste as $lien) {
        $id = $lien['id'];
        $nom = $lien['nom'];
        $is_online = $lien['is_online'];
        $domaine_id = $lien['domaine_id'];
        $sous_domaine = $lien['sous_domaine'];
        $extension = $lien['extension'];
        $promo = $lien['promo'];
        $url = $lien['url'];
        $adresse_cible = $lien['adresse_cible'];
    }


### Api de suppression d'un lien court

=> Créer une requête SupprLienCourtRequest

    /**
     * @var \wellpack\wepak\SupprLienCourtRequest
     */
    $request = new \wellpack\wepak\SupprLienCourtRequest();

=> Passer ensuite les paramètres de filtrage de la requête :

    $request->setKey(1);
    $request->setID(28396);

=> Envoyer la requête et récupérer une réponse spécifique. Ici ce sera une instance d'objet `\wellpack\wepak\SupprLienCourtResponse`.

    /**
     * @var \wellpack\wepak\SupprLienCourtResponse $response
     */
    $response = $service->getSupprLienCourt($request);

=> Vérifier que la réponse est valide. Si ce n'est pas le cas, lire le message d'erreur.

    if (!$response->isValid()) {
        $error = $response->getErrorMessage();
        // Do something with message
        return;
    }

=> Vérifier que la réponse est un succès. Sinon, lire le message d'erreur

    if (!$response->isSuccess()) {
        $message = $response->getMessage();
        // Do something with message
        return;
    }

=> Récupérer les informations contenues dans la réponse

    $message = $response->getMessage();
