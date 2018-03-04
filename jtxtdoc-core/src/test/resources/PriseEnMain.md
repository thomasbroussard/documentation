# Prise en main

Date: 2018-01-31
Updated: 2018-02-06

## Création d'une application simple

* Créer un dossier « app » Dans le dossier de déploiment de votre serveur web.
* Copier le dossier « src » dans ce dossier et renommer-le « vendor ».
* Copier le fichier « SplClassLoader » se trouvant dans « tests/poc-php » à la racine de « app ».
* Créer un fichier « index.php » à la racine de « app ».

## Charger la librairie d'interrogation du service Rest

    <?php
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
    // Déclarer l'epsace de nom « natsystem »
    $classloader->addNamespace('natsystem', $VENDOR_DIR . 'natsystem');

## Instancier le service
    
    // Clé d'utilisation
    $API_KEY = 'd5a8237f-0286-11e8-987b-000d3a2457dd';
    
    // URL d'appel du service
    $URL = 'https://preprod.wepak.wellpack.fr/smsenvoi.php';
    
    
    /**
     * @var \natsystem\wepak\WepackService
     */
    $service = new \natsystem\wepak\WepackService($API_KEY, $URL);


## Interroger les APIS : exemple de l'api "volume"


3 APIs sont actuellement disponibles dans le POC. Nous prendrons pour exemple celle qui permet de demander 


Première étape : créer une requête :

    /**
     * @var \natsystem\wepak\VolumeRequest
     */
    $request = new \natsystem\wepak\VolumeRequest();

Passer ensuite les paramètres de filtrage de la requête :

    $request->setCodePostal(75014);
    $request->setDept(75);
    $request->setCivilite(\natsystem\wepak\ServiceRequest::CIVILITE_HOMME);
    $request->setAgemin(30);
    $request->setAgemax(45);

Envoyer la requête et récupérer une réponse spécifique. Ici ce sera une instance d'objet `\natsystem\wepak\VolumeResponse`.

    /**
     * @var \natsystem\wepak\VolumeResponse $response
     */
    $response = $service->getVolume($request);

Afficher une information présente dans la réponse. Ici, il s'agira du volume global

    echo 'Volume global: ' . $response->getVolume();


