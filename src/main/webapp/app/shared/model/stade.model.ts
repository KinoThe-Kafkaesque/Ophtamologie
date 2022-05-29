import { IClassification } from '@/shared/model/classification.model';
import { IImage } from '@/shared/model/image.model';
import { IPatient } from '@/shared/model/patient.model';
import { IMaladie } from '@/shared/model/maladie.model';

export interface IStade {
  id?: number;
  code?: string | null;
  level?: string | null;
  description?: string | null;
  classifications?: IClassification[] | null;
  images?: IImage[] | null;
  patients?: IPatient[] | null;
  maladie?: IMaladie | null;
}

export class Stade implements IStade {
  constructor(
    public id?: number,
    public code?: string | null,
    public level?: string | null,
    public description?: string | null,
    public classifications?: IClassification[] | null,
    public images?: IImage[] | null,
    public patients?: IPatient[] | null,
    public maladie?: IMaladie | null
  ) {}
}
