This is an assignment: create a widget API

Base part: IMPLEMENTED
- important: I couldn't download jdk from oracle (page down) and as I am on a new computer I don't have older rpms. I compiled it with openjdk11. IF I find a rpm I will change the jdk used, otherwise it would'nt change a lot the behavior.
- api calls: 
	http://localhost:8080 -> basic http page
	http://localhost:8080/widget -> GET gives all widgets. POST allow to create a widget through data in the body on application/json format { "x":X, "y":Y, "width":W, "height":H [,"zindex": Z] } 
	http://localhost:8080/widget/id -> GET gives the widget data, PUT allow to modify the widget using data provided in the body, DELETE delete this widget 
- technical choices: main choice was about the storage. I thought about safe thread version of HashMap or TreeMap.
HashMap using widget id as a key has the advantage of havong average complexity of O(1) for insert/access/delete but getting all widgets and sorting through zindex requires O(nlogn)
TreeMap using zindex as a key (possible as zindex is unique) has the advantage to directly sort by zindex, therefore getting all widgets is only O(n). But access/insert/delete are O(logn)
For a starter I choosed a Treemap for having a better performance at getting all widgets.
In the end I choosed the combination of the 2: it consummes more memory and a bit more performance but in the end we have O(logn) for insert/delete, O(1) for access and O(n) for getting all widgets.

optionnal 1: IMPLEMENTED
- api calls:
	http://localhost:8080/widget[?limit=L&offset=O] -> allow to filter on the requested widget list
- technical choices: mostly done by using Optional on the API controller and using ArrayList to have indexes of widget list

optionnal 2: IMPLEMENTED
- api calls:
	http://localhost:8080/widget[?search_xstart=XS&search_xend=XE&search_ystart=YS&search_yend=YE] -> allow to look at widget list filtered by wdiget contained in the given coordinates.
- technical choices: I thought about 2 options: 
either I divided the grid by some subgrid and make a java reference to widget having a part in each subgrid. Looking for widgets therefore is lookig a widgets in subgrids - lets say there are K candidates, and checking that all constraint are meet (complexity of O(k)),
either I create a Treemap ordered by X coordinate, with a list of widget as a value (X is not unique). Then I make 2 binary search of O(logn) complexity to filter on possible candidates on their x values. K candidates are possible, then I chek for these K candidates (complexity of O(k). 
I opted for the second option (but with real data and analytics I may have not choosed this one) into WidgetLocationList class.

optionnal 3: TODO if I can until end of week
- technical ideas: use a bucket to manage the rates (1 bucket by endpoint). Make an endpoint to manage these rates ? (but need an authentication mechanism for this kind of endpoint)

optionnal 4: TODO if I can until end of week
- technical ideas: I guess I will transform WidgetManager as an interface, and implement memory storage vs sql storage in 2 different files. Then at start I will read a config file and choose which one to use.
