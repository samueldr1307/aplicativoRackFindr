import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Busquedacomponent } from './busquedacomponent';

describe('Busquedacomponent', () => {
  let component: Busquedacomponent;
  let fixture: ComponentFixture<Busquedacomponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [Busquedacomponent],
    }).compileComponents();

    fixture = TestBed.createComponent(Busquedacomponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
