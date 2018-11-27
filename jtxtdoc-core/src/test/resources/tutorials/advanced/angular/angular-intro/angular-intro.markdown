# Angular : Hands On

Angular is one of the most famous client side development (as of 2018). Its strength is to provide an industrial approach to web application, as initially, web was made to share content and not to build applications.  
As one of the Angular developers stated: *Angular is the web if the web had been designed to develop applications*   
Angular is an MVx framework the &#171;x&#187; stands for &#171;whatever&#187;, which means that it is not necessarily a controller as in the MVC case. The &#171;x&#187; is usually replaced by a REST service. In that way, Angular helps to make a cleaner Service Oriented Architecture (SOA).

## Quick Start: Angular IDE

To get started quite quickly with angular, without learning nor understanding all the backbone tools (node, npm etc.), you can start with angular IDE.  
You can get angular IDE by following this link : &#171;Angular IDE&#187;: https://www.genuitec.com/products/angular-ide/download/  
Angular IDE is a revamped version of Eclipse which contains everything to start an angular based IDE.

Once you have downloaded it an started it, you can then create a new "Angular Application"

## Creating a new Angular Application

From Angular IDE, go to Menu File > New > Angular Project 

![](images\angular.ide.newproject.png) 

Give a name to the project (here : &#171;tutorial&#187;), and click finish. Angular IDE may download the missing requirements automatically, which can lead to a long setup, but don&#8217;t worry that&#8217;s only for the first time.

> note that angular uses the dependency resolution system &#171;NPM&#187; (Node Packets Manager), which is really close to how maven works. 

Once you have created it, the project structure looks like this :

![](images\angular.ide.newproject.structure.png)

### The source code location

In the angular projects, the source folder is located under `src/app`. this folder will contain all our source, including :

* the datamodel classes (yes Angular is strongly Object Oriented and Domain Driven Oriented!)
* the services classes
* the html templates that will allow us to build our application
### Launching the application

To launch the application, simply right-click on the project > Run As > Angular Web Application

## Creating components

### the component concept

Angular is based on the &#171;component&#187; concept, the goal of a component is to achieve a specific part of the application. A component will contain both the client side code and the presentation.

> This allows to focus on a precise part of the application without having to deal with a too large/complex amount of code.

### Our first component

To create a component, select `src/app` > (right click) > `New` > `Component` as shown below :  
![](images/angular.ide.newcomponent.png)

We will name this component

Have a look to what is produced :

## Data Binding

## Actions

## Routing

