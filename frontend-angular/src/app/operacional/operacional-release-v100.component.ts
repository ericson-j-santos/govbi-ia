import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-operacional-release-v100',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './operacional-release-v100.component.html',
  styleUrls: ['./operacional-release-v100.component.scss']
})
export class OperacionalReleaseV100Component {
  gates = ['quality_check', 'catalog_version', 'nl_sql_evaluation', 'release_v100', 'e2e_contract', 'homologation_v101'];
}
