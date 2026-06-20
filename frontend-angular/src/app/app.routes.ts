import { Routes } from '@angular/router';
import { PerguntasComponent } from './perguntas/perguntas.component';
import { OperacionalDashboardComponent } from './operacional/operacional-dashboard.component';
import { OperacionalEnterpriseComponent } from './operacional/operacional-enterprise.component';
import { OperacionalEnterpriseV09Component } from './operacional/operacional-enterprise-v09.component';
import { OperacionalReleaseV100Component } from './operacional/operacional-release-v100.component';

export const appRoutes: Routes = [
  { path: '', component: PerguntasComponent },
  { path: 'operacional', component: OperacionalDashboardComponent },
  { path: 'operacional/enterprise', component: OperacionalEnterpriseComponent },
  { path: 'operacional/console', component: OperacionalEnterpriseV09Component },
  { path: 'operacional/release', component: OperacionalReleaseV100Component },
  { path: '**', redirectTo: '' }
];
