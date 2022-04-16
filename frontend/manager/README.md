# Manager

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 13.2.2.

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via a platform of your choice. To use this command, you need to first add a package that implements end-to-end testing capabilities.

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI Overview and Command Reference](https://angular.io/cli) page.

## AppModule  

应该导入 SharedModule、CoreModule、LayoutModule、RouterModule、Angular 模块(例如：BrowserModule、BrowserAnimationsModule、HttpClientModule)；  

## LayoutModule  

应该 导入 SharedModule；  
应该 导出所有 layout component；  
不应该 导入和声明任何路由；  

## RouterModule

应该 导入 SharedModule、CoreModule、LayoutModule以及RouteRoutingModule；  

## CoreModule

应该 只保留providers属性；  

## SharedModule

应该 包含 Angular 通用模块(例如：CommonModule、FormsModule、RouterModule、ReactiveFormsModule)、第三方通用依赖模块、所有组件（自己写的非业务相关的通用组件）、指令、管道；  
应该 导出所有包含模块；  
不应该 有providers属性；  

## Service  

应该 承担应用的数据操作和数据交互；  

## Component

应该 组织视图层的展示和服务计算数据的收集
